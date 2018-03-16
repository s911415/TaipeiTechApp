package app.taipeitech.course.data;

/**
 * Created by Alan on 2015/10/17.
 */
public class Semester {
    private String mYear;
    private String mSemester;

    public Semester(int year, int semester) {
        this(String.valueOf(year), String.valueOf(semester));
    }

    public Semester(String year, String semester) {
        mYear = year;
        mSemester = semester;
    }

    public String getYear() {
        return mYear;
    }

    public String getSemester() {
        return mSemester;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", mYear, mSemester);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof Semester) {
            return ((Semester) obj).mYear.equals(this.mYear) && ((Semester) obj).mSemester.equals(this.mSemester);
        }
        return false;
    }
}
