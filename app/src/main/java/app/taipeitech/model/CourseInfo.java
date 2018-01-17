package app.taipeitech.model;

public class CourseInfo {
    private String courseNo = null;
    private String courseName = null;
    private String courseTeacher = null;
    private String[] courseTimes = null;
    private String[] courseRooms = null;

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public String getCourseName() {
        return courseName;
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

    public String[] getCourseTimes() {
        return courseTimes;
    }

    public void setCourseTime(String[] courseTimes) {
        this.courseTimes = courseTimes;
    }

    public String[] getCourseRooms() {
        return courseRooms;
    }

    public void setCourseRooms(String[] courseRooms) {
        this.courseRooms = courseRooms;
    }
}
