package app.taipeitech.runnable;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import app.taipeitech.utility.NportalConnector;
import app.taipeitech.utility.OCRUtility;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

public class LoginNportalRunnable extends BaseRunnable {
    private WeakReference<Activity> activityWeakReference;
    String account;
    String password;

    public LoginNportalRunnable(String account, String password, Activity activity, Handler handler) {
        super(handler);
        this.account = account;
        this.password = password;
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void run() {
        try {
            String result = NportalConnector.login(account, password);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

    public void oldRun() {
        try {
            Bitmap bmp = NportalConnector.loadAuthcodeImage();
            final CountDownLatch loginLatch = new CountDownLatch(1);
            final Exception[] exception = {null};
            OCRUtility.authOCR(activityWeakReference, bmp, (authCode) -> {
                String result = null;
                try {
                    result = NportalConnector.login(account, password, authCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    exception[0] = e;
                } finally {
                    loginLatch.countDown();
                }
                sendRefreshMessage(result);
            });
            loginLatch.await();
            bmp.recycle();
            if (exception[0] != null) {
                throw exception[0];
            }
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
