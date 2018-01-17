package app.taipeitech;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import app.taipeitech.model.Model;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class MainApplication extends Application {
    private static MainApplication singleton;
    public static String SETTING_NAME = "TaipeiTech";

    public static MainApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        AnalyticsTrackers.initialize(this);
        singleton = this;
        Model.getInstance();
        Intent check_intent = new Intent(this, ActivityCheckReceiver.class);
        check_intent.putExtra("action", "app.taipeitech.action.ACTIVITY_CHECK");
        sendBroadcast(check_intent);
    }

    private static byte[] _key = null;

    private static byte[] getKey() {
        if (_key == null) {
            StringBuilder sb = new StringBuilder(Settings.Secure.getString(singleton.getContentResolver(), Settings.Secure.ANDROID_ID));
            while (sb.length() < 16) {
                sb.insert(0, '0');
            }
            String keyStr = sb.toString() + sb.reverse().toString();
            keyStr = keyStr.replaceAll("[^0-9A-Fa-f]", "");
            _key = keyStr.getBytes();
        }

        return _key;
    }

    public static String readSetting(String settingName) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        return settings.getString(settingName, "");
    }

    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    public static String readSettingAndDecrypt(String settingName) {
        String cipherText = readSetting(settingName);
        try {
            byte[] cipher = Base64.decode(cipherText, Base64.DEFAULT);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(getKey(), "AES");
            Cipher mCipher = Cipher.getInstance(AES_MODE);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec);
            byte[] plain = mCipher.doFinal(cipher);

            return new String(plain, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static void writeSetting(String settingName, String value) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        settings.edit().putString(settingName, value).apply();
    }

    public static void writeSettingWithEncrypt(String settingName, String value) {
        try {
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(getKey(), "AES");
            Cipher mCipher = null;
            mCipher = Cipher.getInstance(AES_MODE);
            mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec);

            byte[] cipher = mCipher.doFinal(value.getBytes("UTF-8"));
            String cipherText = Base64.encodeToString(cipher, Base64.DEFAULT);
            writeSetting(settingName, cipherText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void clearSettings(String settingName) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        settings.edit().remove(settingName).apply();
    }
}
