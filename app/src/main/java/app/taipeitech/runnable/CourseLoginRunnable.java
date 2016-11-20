package app.taipeitech.runnable;

import android.os.Handler;

import app.taipeitech.utility.CourseConnector;

public class CourseLoginRunnable extends BaseRunnable {
    public CourseLoginRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            String result = CourseConnector.loginCourse();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}
