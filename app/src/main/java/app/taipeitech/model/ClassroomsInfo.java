package app.taipeitech.model;

import app.taipeitech.classroom.data.Classroom;
import app.taipeitech.course.data.Semester;

import java.util.List;

public class ClassroomsInfo {
    public final Semester sem;
    public final List<Classroom> classroomList;

    public ClassroomsInfo(final Semester sem, final List<Classroom> list) {
        this.sem = sem;
        this.classroomList = list;
    }
}
