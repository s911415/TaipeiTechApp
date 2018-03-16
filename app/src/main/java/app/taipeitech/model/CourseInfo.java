package app.taipeitech.model;

import android.util.SparseArray;

import java.util.HashSet;
import java.util.List;

public class CourseInfo {
    private String courseNo = null;
    private String courseName = null;
    private String courseTeacher = null;
    private String courseClass = null;
    private SparseArray<HashSet<String>> courseTimes = null;
    private SparseArray<String> courseRooms = null;

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseClass(String courseClass) {
        this.courseClass = courseClass.trim();
    }

    public String getCourseClass() {
        return courseClass;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName.trim();
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher.trim();
    }

    public SparseArray<HashSet<String>> getCourseTimes() {
        initTimes();
        return courseTimes;
    }

    public void setCourseTime(int week, String courseTimes) {
        initTimes();
        String[] timeStr = courseTimes.split("\\s+");
        HashSet<String> hashSet = this.courseTimes.get(week);
        hashSet.clear();
        for (String s : timeStr) {
            s = s.trim();
            if (s.isEmpty()) continue;

            hashSet.add(s);
        }
    }

    public void addCourseTime(int week, String courseTime) {
        initTimes();
        HashSet<String> hashSet = this.courseTimes.get(week);
        hashSet.add(courseTime);
    }

    public void setCourseTime(String[] courseTimes) {
        initTimes();

        for (int i = 0; i < 7 && i < courseTimes.length; i++) {
            setCourseTime(i, courseTimes[i]);
        }
    }

    public SparseArray<String> getCourseRooms() {
        initRooms();
        return courseRooms;
    }

    public void setCourseRooms(String[] courseRooms) {
        initRooms();

        for (int i = 0; i < 7; i++) {
            if (!courseRooms[i].isEmpty()) {
                this.courseRooms.put(i, courseRooms[i]);
            }
        }
    }

    private void initTimes() {
        if (this.courseTimes == null) {
            this.courseTimes = new SparseArray<>();
            for (int i = 0; i < 7; i++)
                this.courseTimes.put(i, new HashSet<String>());
        } else {
            for (int i = 0; i < 7; i++) {
                if (this.courseTimes.get(i) instanceof List) {
                    this.courseTimes.put(i, new HashSet<>(this.courseTimes.get(i)));
                }
            }
        }
    }

    private void initRooms() {
        if (this.courseRooms == null) {
            this.courseRooms = new SparseArray<>();
            for (int i = 0; i < 7; i++)
                this.courseRooms.put(i, "");
        }
    }
}
