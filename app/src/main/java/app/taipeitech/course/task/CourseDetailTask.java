package app.taipeitech.course.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import app.taipeitech.course.CourseDetailInterface;
import app.taipeitech.course.CourseFragment;
import app.taipeitech.model.Model;
import app.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class CourseDetailTask extends AsyncTask<String, Void, Object> {
    private WeakReference<CourseDetailInterface> mCourseFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public CourseDetailTask(CourseDetailInterface fragment) {
        mCourseFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CourseDetailInterface fragment = mCourseFragmentWeakReference.get();
        if (fragment != null && !NportalConnector.isLogin()) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "登入校園入口網站中...");
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        Object result;
        try {
            if (!NportalConnector.isLogin()) {
                String account = Model.getInstance().getAccount();
                String password = Model.getInstance().getPassword();
                WeakReference<Activity> activityWeakReference = new WeakReference<>(mCourseFragmentWeakReference.get().getActivity());
                NportalConnector.login(activityWeakReference, account, password);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (mProgressDialogWeakReference != null) {
            ProgressDialog progressDialog = mProgressDialogWeakReference.get();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
        CourseDetailInterface fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            fragment.startCourseDetail(object);
        }
    }
}
