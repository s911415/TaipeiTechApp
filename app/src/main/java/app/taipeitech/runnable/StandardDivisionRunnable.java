package app.taipeitech.runnable;

import android.os.Handler;

import app.taipeitech.utility.CreditConnector;

import java.util.ArrayList;

public class StandardDivisionRunnable extends BaseRunnable {
    private String year;

    public StandardDivisionRunnable(Handler handler, String year) {
        super(handler);
        this.year = year;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CreditConnector.getDivisionList(year);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}
