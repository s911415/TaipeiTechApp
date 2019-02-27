package app.taipeitech.classroom.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import app.taipeitech.classroom.ClassroomFragment;
import app.taipeitech.model.Model;
import app.taipeitech.utility.Constants;
import app.taipeitech.utility.CourseConnector;
import app.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class FetchClassroomUsageTask extends AsyncTask<String, Void, Object> {
    private WeakReference<ClassroomFragment> mClassroomFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public FetchClassroomUsageTask(ClassroomFragment fragment) {
        mClassroomFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ClassroomFragment fragment = mClassroomFragmentWeakReference.get();
        if (fragment != null) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "課表查詢中...");
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        } else {
            cancel(true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        int retryCount = 0;
        Object result;
        do {
            try {
                if (!NportalConnector.isLogin()) {
                    String account = Model.getInstance().getAccount();
                    String password = Model.getInstance().getPassword();
                    WeakReference<Activity> activityWeakReference = new WeakReference<>(mClassroomFragmentWeakReference.get().getActivity());
                    NportalConnector.login(activityWeakReference, account, password);
                    CourseConnector.loginCourse();
                }
                if (!CourseConnector.isLogin()) {
                    CourseConnector.loginCourse();
                }
                result = CourseConnector.getClassroomCourses(params[0], params[1], params[2]);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                retryCount++;
            }
        } while (retryCount <= Constants.RETRY_MAX_COUNT_INT);
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);

        ProgressDialog progressDialog = mProgressDialogWeakReference.get();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        ClassroomFragment fragment = mClassroomFragmentWeakReference.get();
        if (fragment != null) {
            fragment.obtainCourseList(object);

        }
    }
}
