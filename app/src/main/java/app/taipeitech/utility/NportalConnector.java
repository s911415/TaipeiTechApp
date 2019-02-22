package app.taipeitech.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import app.taipeitech.runnable.LoginNportalRunnable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class NportalConnector {
    private static boolean isLogin = false;
    private static final String IMAGE_URI = "https://nportal.ntut.edu.tw/authImage.do";
    private static final String LOGIN_URI = "https://nportal.ntut.edu.tw/login.do";
    public static final String NPORTAL_URI = "https://nportal.ntut.edu.tw/index.do";

    public static void login(String id, String password, Activity activity, Handler handler) {
        Thread login_thread = new Thread(new LoginNportalRunnable(id, password,
                activity,
                handler));
        login_thread.start();
    }

    public static void login(WeakReference<Activity> activityRef, String account, String password) throws Exception {
        try {

            final Bitmap bmp = NportalConnector.loadAuthcodeImage();
            final CountDownLatch loginLatch = new CountDownLatch(1);
            final Exception[] exception = {null};
            OCRUtility.authOCR(activityRef, bmp, (authCode) -> {
                try {
                    NportalConnector.login(account, password, authCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    exception[0] = e;
                } finally {
                    loginLatch.countDown();
                }
            });
            loginLatch.await();
            bmp.recycle();

            if (exception[0] != null) throw exception[0];
        } catch (Exception e) {
            reset();
            e.printStackTrace();
            throw new Exception("登入校園入口網站時發生錯誤");
        }
    }

    public static String login(String muid, String mpassword, String authcode)
            throws Exception {
        isLogin = false;
        HashMap<String, String> params = new HashMap<>();
        params.put("muid", muid);
        params.put("mpassword", mpassword);
        params.put("authcode", authcode);
        params.put("forceMobile", "mobile");
        String md5Code = getMD5Code(muid, mpassword);
        params.put("md5Code", md5Code);
        String result;
        try {
            result = Connector.getDataByPost(LOGIN_URI, params, "utf-8", NPORTAL_URI + "?thetime=" + String.valueOf(System.currentTimeMillis()));
            Connector.getDataByGet("https://nportal.ntut.edu.tw/myPortalHeader.do", "utf-8");
            Connector.getDataByGet("https://nportal.ntut.edu.tw/aptreeBox.do", "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("入口網站登入時發生錯誤");
        }
        if (result.contains("帳號或密碼錯誤")) {
            throw new Exception("帳號或密碼錯誤");
        } else if (result.contains("驗證碼")) {
            throw new Exception("驗證碼錯誤");
        }
        isLogin = true;
        return result;
    }

    public static Bitmap loadAuthcodeImage() throws Exception {
        try {
            Connector.getDataByGet(NPORTAL_URI,
                    "big5");
            InputStream input_stream = Connector.getInputStreamByGet(IMAGE_URI, null);
            Bitmap bitmap = BitmapFactory.decodeStream(input_stream);
            if (bitmap == null) {
                throw new Exception();
            } else {
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("驗證碼讀取時發生錯誤");
        }
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static void reset() {
        isLogin = false;
    }

    private static String getMD5Code(String account, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] key = password.getBytes();
        byte[] input = account.getBytes();

        if (input.length % 8 != 0) { //not a multiple of 8
            //create a new array with a size which is a multiple of 8
            byte[] padded = new byte[input.length + 8 - (input.length % 8)];

            //copy the old array into it
            System.arraycopy(input, 0, padded, 0, input.length);
            int padding = padded.length - input.length;
            for (int i = 1; i <= padding; i++) {
                padded[padded.length - i] = (byte) (char) padding;
            }
            input = padded;
        }

        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding", "BC");
        SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] output = cipher.doFinal(input);
        return Base64.encodeToString(output, Base64.DEFAULT).trim();
    }
}