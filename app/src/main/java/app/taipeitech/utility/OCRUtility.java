package app.taipeitech.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import app.taipeitech.R;

import java.lang.ref.WeakReference;

public class OCRUtility {

    @Deprecated
    private static byte[][] bitmap2grayByteArry(Bitmap bm) {
        byte[][] grayImage = new byte[bm.getHeight()][bm.getWidth()];
        for (int i = 0; i < bm.getWidth(); i++) {
            for (int j = 0; j < bm.getHeight(); j++) {
                if (bm.getPixel(i, j) == Color.WHITE) {
                    grayImage[j][i] = 0;
                } else {
                    grayImage[j][i] = 1;
                }
            }
        }
        return grayImage;
    }

    @Deprecated
    public static String authOCR(byte[][] grayArray, int width, int height) {
        int start = -1;
        int end = -1;
        String text = "";
        for (int i = 0; i < width; i++) {
            Boolean isBackground = true;
            for (int j = 0; j < height; j++) {
                if (grayArray[j][i] == 0) {
                    isBackground = false;
                    if (start == -1) {
                        start = i;
                    }
                }
            }
            if (isBackground == true && start != -1) {
                end = i - 1;
            }
            if (start != -1 && end != -1) {
                String wordArray = splitWord(grayArray, start, end, height);
                text += recognizeWord(wordArray);
                start = -1;
                end = -1;
            }
        }
        return text;
    }

    public static void authOCR(@NonNull WeakReference<Activity> activityRef, Bitmap bitmap, OCRCallback runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
        builder.setTitle(R.string.nportal_auth_code_title);
        LayoutInflater li = LayoutInflater.from(activityRef.get());
        final View authDialogView = li.inflate(R.layout.dialog_auth_code, null);
        builder.setView(authDialogView);

        builder.setPositiveButton(R.string.nportal_auth_code_ok, (dialog, which) -> {
            EditText authCodeView = authDialogView.findViewById(R.id.authCodeInput);
            String authCode = authCodeView.getText().toString();
            try {
                InputMethodManager imm = (InputMethodManager) (activityRef.get().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (imm != null) {
                    imm.hideSoftInputFromWindow(authCodeView.getWindowToken(), 0);
                }
                dialog.dismiss();
                new Thread(() -> runnable.run(authCode)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton(R.string.nportal_auth_code_cancel, (dialog, which) -> {
            dialog.cancel();
        });
        builder.setOnCancelListener((v) -> {
            try {
                new Thread(() -> runnable.run(null)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        activityRef.get().runOnUiThread(() -> {
            builder.show();
            ((ImageView) authDialogView.findViewById(R.id.authCodeImageView)).setImageBitmap(bitmap);
            EditText authCodeInput = authDialogView.findViewById(R.id.authCodeInput);
            authCodeInput.requestFocus();
            authCodeInput.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) (activityRef.get().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (imm != null) {
                    imm.showSoftInput(authCodeInput, 0);
                }
            }, 200);
        });
    }

    public interface OCRCallback {
        void run(String authCode);
    }

    private static final String[] LETTERS = {"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z"};
    private static final String[] LETTER_ARRAY = {
            "110000001111000000001110111100001111111100011111111000111100000001100000000010000111000100011110001000111000010000000000010000011000",
            "000111111111000111111111000111111111000111111111000111111111000100000111000000000011000011110001000111111000000111111000000111111000000111111000000111111000000111110000000011100001000000000011000100000111",
            "111000000111000000001000011110000011111100011111110001111111000111111100011111110000111111100001111011000000001110000001",
            "111111111000111111111000111111111000111111111000111111111000111000001000110000000000100001110000000011111000000111111000000111111000000111111000000111111000000111111000100011100000100000000000111000011000",
            "111000001111100000000110001110001000111110000001111100000000000000000000000000001111111100011111111100011111101100000000011100000001",
            "111100000111000000110001111110001111110001111000000001000000001110001111110001111110001111110001111110001111110001111110001111110001111110001111110001111",
            "111000001000110000000000100001110000000011111000000111111000000111111000000111111000000111111000000111111000100011100000100000000000111000011000111111111000100111110001100000000011110000000111",
            "00011111110001111111000111111100011111110001111111000110000100010000010000011000000011100000011110000001111000000111100000011110000001111000000111100000011110000001111000",
            "000000111111000000000000000000000000000000000000",
            "11110001111000111111111111111111000111100011110001111000111100011110001111000111100011110001111000111100011110001111000011100000000011000011",
            "000111111111000111111111000111111111000111111111000111111111000111110011000111100111000111001111000110011111000100111111000000011111000100001111000110001111000111000111000111000011000111100001000111110000",
            "000000000000000000000000000000000000000000000000000",
            "000110000111000010001000000100000000001110000111000000111100011110000001111000111100000011110001111000000111100011110000001111000111100000011110001111000000111100011110000001111000111100000011110001111000",
            "000110000100010000010000011000000011100000011110000001111000000111100000011110000001111000000111100000011110000001111000",
            "111000000111110000000011100011110001000011110000000111111000000111111000000111111000000111111000000011110000100011110001110000000011111000000111",
            "000100000111000000000011000011110001000111111000000111111000000111111000000111111000000111111000000111110000000011100001000000000011000100000111000111111111000111111111000111111111000111111111",
            "111000001000110000000000100001110000000011111000000111111000000111111000000111111000000111111000000111111000100011100000100000000000111000011000111111111000111111111000111111111000111111111000",
            "000110000001000000000111000011110001111100011111000111110001111100011111000111110001111100011111",
            "110000111000000100011101000111110000011110000011110000011111000011111000011110000000000110000011",
            "10001111000111000000000000001000111100011110001111000111100011110001111000111100001111000001110000",
            "000111100000011110000001111000000111100000011110000001111000000111100000011110000001110000000110000000000010001000011000",
            "000111111100100011111001100011111001100011111001110001110011110001110011110000100111111000100111111000100111111100001111111100001111111100001111",
            "000111100001111000001111000011110010001110000111001100011100001110011000110010001100111000100100010011110001001000100111100010010001001111000101110010011111000011100001111110000111000011111100001110000111",
            "000011111001000111100111000110011110000001111110000011111100001111111100001111110000011111100000011110011000111001111000100111110000",
            "000111111100100011111001100011111001100001110001110001110011110000100011111000100111111000100111111100001111111100001111111100001111111110011111111100011111111100111111111000111111110000111111",
            "000000000000000000000011111110000111111000011111100001111110000111111000011111100001111110000111111000011111110000000000000000000000"};

    private static String recognizeWord(String wordArray) {
        for (int i = 0; i < LETTERS.length; i++) {
            if (wordArray.equals(LETTER_ARRAY[i])) {
                return LETTERS[i];
            }
        }
        return null;
    }

    private static String splitWord(byte[][] grayArray, int start, int end,
                                    int height) {
        int top = -1;
        int bottom = -1;
        String result = "";

        for (int j = 0; j < height; j++) {
            for (int i = start; i <= end; i++) {
                if (grayArray[j][i] == 0) {
                    if (top == -1) {
                        top = j;
                        break;
                    }
                }
            }
        }

        for (int j = height - 1; j >= 0; j--) {
            for (int i = start; i <= end; i++) {
                if (grayArray[j][i] == 0) {
                    bottom = j;
                    break;
                }
            }
            if (bottom != -1) {
                break;
            }
        }

        if (top != -1 && bottom != -1) {
            for (int j = top; j <= bottom; j++) {
                for (int i = start; i <= end; i++) {
                    if (grayArray[j][i] == 0) {
                        result += "0";
                    } else if (grayArray[j][i] == 1) {
                        result += "1";
                    }
                }
            }
        }
        return result;
    }

}
