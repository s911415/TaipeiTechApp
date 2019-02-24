package app.taipeitech.runnable;

import android.app.Activity;
import android.os.Handler;

import app.taipeitech.model.Model;
import app.taipeitech.model.YearCalendar;
import app.taipeitech.utility.CalendarConnector;
import app.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

public class CalendarRunnable extends BaseRunnable {
    private WeakReference<Activity> activityWeakReference;
    public CalendarRunnable(Activity activity, Handler handler) {
        super(handler);
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void run() {
        try {
            if(!NportalConnector.isLogin()) {
                String account = Model.getInstance().getAccount();
                String password = Model.getInstance().getPassword();
                NportalConnector.login(activityWeakReference, account, password);
            }
            YearCalendar result = CalendarConnector.getEventList();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
