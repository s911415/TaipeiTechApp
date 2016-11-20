package app.taipeitech.runnable;

import android.os.Handler;

import app.taipeitech.utility.CreditConnector;

import java.util.ArrayList;

public class StandardYearRunnable extends BaseRunnable {
    public StandardYearRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CreditConnector.getYearList();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}
