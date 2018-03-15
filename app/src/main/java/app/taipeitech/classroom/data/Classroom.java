package app.taipeitech.classroom.data;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Classroom {
    public final static Comparator<Classroom> comparator = new Comparator<Classroom>() {
        public int compare(Classroom obj1, Classroom obj2) {
            return obj1._code - obj2._code;
        }
    };

    private final static Pattern p = Pattern.compile("([A-Za-z0-9_]+)");

    private final String _name;
    private final int _code;

    public Classroom(String code, String name) {
        this(Integer.parseInt(code), name);
    }

    public Classroom(int code, String name) {
        this._code = code;
        Matcher m = p.matcher(name);
        if (m.find()) {
            this._name = m.replaceAll(" $1 ").trim();
        } else {
            this._name = name;
        }
    }

    public int getCode() {
        return _code;
    }

    public String getName() {
        return _name;
    }

    public String getBuildingText() {
        if (_name.startsWith("綜") && _name.endsWith("演講廳")) {
            return "綜科";
        }
        return _name.substring(0, 2);
    }

    @Override
    public String toString() {
        return this._name;
    }
}
