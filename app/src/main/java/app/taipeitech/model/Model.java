package app.taipeitech.model;

import android.support.annotation.Nullable;
import app.taipeitech.MainApplication;
import app.taipeitech.course.data.Semester;
import com.google.gson.Gson;

public class Model {
    private static final String ACCOUNT_NAME = "account";
    private static final String PASSWORD_NAME = "password";
    private static final String STUDENT_COURSE_NAME = "studentCourse";
    private static final String STUDENT_CREDIT_NAME = "studentCredit";
    private static final String STANDARD_CREDIT_NAME = "standardCredit";
    private static final String APP_CALENDAR_NAME = "appCalendar";
    private static final String CLASSROOMS_NAME = "classroomList";
    private static final String ACTIVITY_ARRAY_NAME = "activityArray";
    private volatile static Model instance = null;
    private StudentCourse studentCourse = null;
    private StudentCredit studentCredit = null;
    private StandardCredit standardCredit = null;
    private ClassroomsInfo classroomsInfo = null;
    private YearCalendar yearCalendar = null;
    private ActivityList activityArray = null;

    private Model() {
        initialize();
    }

    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }
        return instance;
    }

    private void initialize() {
        studentCourse = readObjectSetting(STUDENT_COURSE_NAME,
                StudentCourse.class);
        studentCredit = readObjectSetting(STUDENT_CREDIT_NAME,
                StudentCredit.class);
        standardCredit = readObjectSetting(STANDARD_CREDIT_NAME,
                StandardCredit.class);
        yearCalendar = readObjectSetting(APP_CALENDAR_NAME, YearCalendar.class);
        classroomsInfo = readObjectSetting(CLASSROOMS_NAME, ClassroomsInfo.class);
        activityArray = readObjectSetting(ACTIVITY_ARRAY_NAME,
                ActivityList.class);
    }

    private <T> T readObjectSetting(String type_string, Class<T> classOfT) {
        try {
            Gson gson = new Gson();
            String json = MainApplication.readSetting(type_string);
            if (json != null && json.length() > 0) {
                return gson.fromJson(json, classOfT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveObjectSetting(String type_string, Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        MainApplication.writeSetting(type_string, json);
    }

    public String getAccount() {
        return MainApplication.readSettingAndDecrypt(ACCOUNT_NAME);
    }

    public String getPassword() {
        return MainApplication.readSettingAndDecrypt(PASSWORD_NAME);
    }

    public void saveAccountPassword(String account, String password) {
        MainApplication.writeSettingWithEncrypt(ACCOUNT_NAME, account);
        MainApplication.writeSettingWithEncrypt(PASSWORD_NAME, password);
    }

    public void deleteAccountPassword() {
        MainApplication.clearSettings(ACCOUNT_NAME);
        MainApplication.clearSettings(PASSWORD_NAME);
    }

    public StandardCredit getStandardCredit() {
        return standardCredit;
    }

    public void setStandardCredit(StandardCredit standardCredit) {
        this.standardCredit = standardCredit;
    }

    public void deleteStandardCredit() {
        MainApplication.clearSettings(STANDARD_CREDIT_NAME);
        standardCredit = null;
    }

    public void saveStandardCredit() {
        saveObjectSetting(STANDARD_CREDIT_NAME, standardCredit);
    }

    public StudentCredit getStudentCredit() {
        return studentCredit;
    }

    public void setStudentCredit(StudentCredit studentCredit) {
        this.studentCredit = studentCredit;
    }

    public void deleteStudentCredit() {
        MainApplication.clearSettings(STUDENT_CREDIT_NAME);
        studentCredit = null;
    }

    public void saveStudentCredit() {
        saveObjectSetting(STUDENT_CREDIT_NAME, studentCredit);
    }

    public StudentCourse getStudentCourse() {
        return studentCourse;
    }

    @Nullable
    public ClassroomsInfo getClassroomsInfo(Semester sem) {
        if (classroomsInfo != null) {
            if (classroomsInfo.sem.equals(sem)) {
                return classroomsInfo;
            }
        }

        return null;
    }

    public void saveClassroomsInfo(ClassroomsInfo info) {
        saveObjectSetting(CLASSROOMS_NAME, info);
        this.classroomsInfo = info;
    }

    public void deleteClassroomsInfo() {
        MainApplication.clearSettings(CLASSROOMS_NAME);
        this.classroomsInfo = null;
    }

    public void setStudentCourse(StudentCourse studentCourse) {
        this.studentCourse = studentCourse;
    }

    public void deleteStudentCourse() {
        MainApplication.clearSettings(STUDENT_COURSE_NAME);
        studentCourse = null;
    }

    public void saveStudentCourse() {
        saveObjectSetting(STUDENT_COURSE_NAME, studentCourse);
    }

    public YearCalendar getYearCalendar() {
        return yearCalendar;
    }

    public void setYearCalendar(YearCalendar yearCalendar) {
        this.yearCalendar = yearCalendar;
    }

    public void saveYearCalendar() {
        saveObjectSetting(APP_CALENDAR_NAME, yearCalendar);
    }
/*
    public ActivityList getActivityArray() {
        if (activityArray != null) {
            activityArray.checkActivity();
            if (activityArray.size() == 0) {
                setActivityArray(null);
                saveActivityArray();
            }
        }
        return activityArray;
    }

    public void setActivityArray(ActivityList activitArray) {
        this.activityArray = activitArray;
    }

    public void saveActivityArray() {
        saveObjectSetting(ACTIVITY_ARRAY_NAME, activityArray);
    }
    */
}
