package app.taipeitech.model;

import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class CourseCollectionInfo extends CourseInfo {
    private ArrayList<CourseInfo> _list = new ArrayList<>();

    public void addCourseInfo(CourseInfo c) {
        _list.add(c);
    }

    public String getCourseNo() {
        if (isExactlyOne()) {
            return _list.get(0).getCourseNo();
        } else {
            LinkedList<String> ret = new LinkedList<>();
            for (CourseInfo c : _list) {
                ret.add(c.getCourseNo());
            }
            return TextUtils.join(", ", ret);
        }
    }

    public String getCourseName() {
        return _list.get(0).getCourseName();
    }

    public String getCourseTeacher() {
        return "";
    }

    public SparseArray<HashSet<String>> getCourseTimes() {
        return _list.get(0).getCourseTimes();
    }

    public SparseArray<String> getCourseRooms() {
        return _list.get(0).getCourseRooms();
    }

    public CourseInfo getCourseAt(int i) {
        return _list.get(i);
    }

    public ArrayList<CourseInfo> getCourses() {
        return _list;
    }

    public void setCourseNo(String courseNo) {

    }

    public void setCourseName(String courseName) {
    }

    public void setCourseTeacher(String courseTeacher) {
    }


    public void setCourseTime(String[] courseTimes) {
    }


    public void setCourseRooms(String[] courseRooms) {
    }

    public boolean isExactlyOne() {
        return _list.size() == 1;
    }
}
