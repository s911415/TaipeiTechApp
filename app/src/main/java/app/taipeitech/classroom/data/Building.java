package app.taipeitech.classroom.data;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private final String _buildingName;
    private final ArrayList<Classroom> _classRooms = new ArrayList<>();

    public Building(String bName) {
        this._buildingName = bName;
    }

    public String getBuildingName() {
        return _buildingName;
    }

    public void add(Classroom c) {
        _classRooms.add(c);
    }

    public List<Classroom> getClassrooms() {
        return _classRooms;
    }
}
