package app.taipeitech.classroom.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import app.taipeitech.classroom.ClassroomFragment;
import app.taipeitech.course.data.Semester;
import app.taipeitech.model.ClassroomsInfo;
import app.taipeitech.model.Model;
import app.taipeitech.utility.Constants;
import app.taipeitech.utility.CourseConnector;
import app.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class SearchClassroomTask extends AsyncTask<String, Void, Object> {
    private WeakReference<ClassroomFragment> mClassroomFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public SearchClassroomTask(ClassroomFragment fragment) {
        mClassroomFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ClassroomFragment fragment = mClassroomFragmentWeakReference.get();
        if (fragment != null) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "教室查詢中...");
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
                    NportalConnector.login(account, password);
                }
                if (!CourseConnector.isLogin()) {
                    CourseConnector.loginCourse();
                }
                result = new ClassroomsInfo(new Semester(params[0], params[1]), CourseConnector.getClassrooms(params[0], params[1]));
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
            if (object instanceof ClassroomsInfo) {
                Model.getInstance().saveClassroomsInfo((ClassroomsInfo) object);
                fragment.onClassroomListReady(((ClassroomsInfo) object).classroomList);
            }
        }
    }
}
