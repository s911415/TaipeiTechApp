package app.taipeitech.classroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import app.taipeitech.BaseFragment;
import app.taipeitech.R;
import app.taipeitech.classroom.component.ClassroomListDialog;
import app.taipeitech.classroom.component.ClassroomSpinner;
import app.taipeitech.classroom.component.CourseListDialog;
import app.taipeitech.classroom.data.Building;
import app.taipeitech.classroom.data.Classroom;
import app.taipeitech.classroom.task.FetchClassroomUsageTask;
import app.taipeitech.classroom.task.SearchClassroomTask;
import app.taipeitech.course.CourseDetailActivity;
import app.taipeitech.course.CourseDetailInterface;
import app.taipeitech.course.CourseTableLayout;
import app.taipeitech.course.CourseTableLayout.TableInitializeListener;
import app.taipeitech.course.data.Semester;
import app.taipeitech.course.task.CourseDetailTask;
import app.taipeitech.model.ClassroomsInfo;
import app.taipeitech.model.CourseCollectionInfo;
import app.taipeitech.model.CourseInfo;
import app.taipeitech.model.Model;
import app.taipeitech.utility.Utility;
import app.taipeitech.utility.WifiUtility;

import java.util.*;

public class ClassroomFragment extends BaseFragment implements OnClickListener, CourseDetailInterface,
        TableInitializeListener {
    private static String[] TIME_ARRAY;
    private CourseTableLayout courseTable;
    private Building currentBuilding = null;
    private Classroom classroom = null;
    private ClassroomSpinner classroomSpinner;
    private BuildingSelector mBuildingSelector;
    private static View fragmentView;
    private final static String ALL_TEXT = "[全部]";
    private final Semester curSem = Utility.getCurrentSemesterInfo();
    private String selectedCourseNo = "";
    private CourseListDialog _courseListDialog;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TIME_ARRAY = getResources().getStringArray(R.array.time_array);
        fragmentView = inflater.inflate(R.layout.fragment_classroom, container,
                false);
        fragmentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeSoftKeyboard();
            }
        });
        initCourseTable();
        mBuildingSelector = (BuildingSelector) fragmentView.findViewById(R.id.building);
        mBuildingSelector.setOnBuildingSelectedListener(buildingSelectedLis);
        classroomSpinner = fragmentView.findViewById(R.id.classroomSpinner);
        classroomSpinner.setOnItemSelected(onClassroomSelected);

        return fragmentView;
    }


    private void fetchBuildingTable() {
        ClassroomsInfo savedInfo = Model.getInstance().getClassroomsInfo(curSem);
        if (savedInfo == null) {
            if (WifiUtility.isNetworkAvailable(getActivity())) {
                closeSoftKeyboard();
                if (Utility.checkAccount(getActivity())) {
                    SearchClassroomTask searchCourseTask = new SearchClassroomTask(this);
                    searchCourseTask.execute(curSem.getYear(), curSem.getSemester());
                }
            } else {
                Toast.makeText(getActivity(), R.string.check_network_available,
                        Toast.LENGTH_LONG).show();
            }

        } else {
            onClassroomListReady(savedInfo.classroomList);
        }

    }

    private void initCourseTable() {
        courseTable = (CourseTableLayout) fragmentView
                .findViewById(R.id.courseTable);
        courseTable.setTableInitializeListener(this);
        courseTable.setOnCourseClickListener(this);
        courseTable.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    closeSoftKeyboard();
                }
                return false;
            }
        });
    }

    private void showCourseTable() {
        if (classroom == null) {
            Toast.makeText(getActivity(), R.string.no_classroom_selected,
                    Toast.LENGTH_LONG).show();
        } else if (WifiUtility.isNetworkAvailable(getActivity())) {
            closeSoftKeyboard();
            if (Utility.checkAccount(getActivity())) {
                FetchClassroomUsageTask task = new FetchClassroomUsageTask(this);
                task.execute(curSem.getYear(), curSem.getSemester(), String.valueOf(classroom.getCode()));
            }
        } else {
            Toast.makeText(getActivity(), R.string.check_network_available,
                    Toast.LENGTH_LONG).show();
        }

    }

    private LinkedHashMap<String, Building> getBuildingList(List<Classroom> classrooms) {
        LinkedHashMap<String, Building> ret = new LinkedHashMap<>();
        Building _all = new Building(ALL_TEXT);
        ret.put(_all.getBuildingName(), _all);

        for (final Classroom c : classrooms) {
            final String buildingText = c.getBuildingText();
            Building building = ret.get(buildingText);
            if (building == null) {
                building = new Building(buildingText);
                ret.put(buildingText, building);
            }
            building.add(c);
            _all.add(c);
        }

        return ret;
    }

    public void onClassroomListReady(List<Classroom> classrooms) {
        LinkedHashMap<String, Building> buildingLinkedHashMap = getBuildingList(classrooms);
        LinkedList<Building> list = new LinkedList<>();
        list.addAll(buildingLinkedHashMap.values());
        mBuildingSelector.setBuildingList(list);
        Building b = buildingLinkedHashMap.get(ALL_TEXT);
        mBuildingSelector.setText(b);
        classroomSpinner.setDataSet(b.getClassrooms());
    }

    private BuildingSelector.OnBuildingSelectedListener buildingSelectedLis = new BuildingSelector.OnBuildingSelectedListener() {
        @Override
        public void onBuildingSelected(Building building) {
            currentBuilding = building;
            classroomSpinner.setDataSet(building.getClassrooms());
        }
    };

    public DialogInterface.OnClickListener courseDetailDialogLis = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            if (WifiUtility.isNetworkAvailable(getActivity())) {
                if (Utility.checkAccount(getActivity())) {
                    CourseDetailTask courseDetailTask = new CourseDetailTask(ClassroomFragment.this);
                    courseDetailTask.execute(selectedCourseNo);
                }
            } else {
                Toast.makeText(getActivity(),
                        R.string.check_network_available, Toast.LENGTH_LONG)
                        .show();
            }
        }
    };


    public void obtainCourseList(Object object) {
        if (object instanceof ArrayList) {
            showCourse((ArrayList<CourseInfo>) object);
        }
    }

    private void showCourse(ArrayList<CourseInfo> courseList) {
        courseTable.showCourse(courseList);
    }

    private ClassroomListDialog.SearchableItem<Classroom> onClassroomSelected = new ClassroomListDialog.SearchableItem<Classroom>() {
        @Override
        public void onSearchableItemClicked(Classroom item, int position) {
            classroom = item;
            showCourseTable();
        }
    };

    @Override
    public void onClick(final View view) {
        CourseCollectionInfo item = (CourseCollectionInfo) view.getTag(R.id.course);
        final int week = (int) view.getTag(R.id.week);
        if (item.isExactlyOne()) {
            showInfoDialog(view.getId(), week, item.getCourseName(), item.getCourseAt(0));
        } else {
            _courseListDialog = CourseListDialog.newInstance(item.getCourses());
            _courseListDialog.setTitle(getString(R.string.course_detail_title));
            _courseListDialog.setPositiveButton(getString(R.string.close_drawer));
            _courseListDialog.setOnItemClickListener(new CourseListDialog.ClickableItem() {
                @Override
                public void onItemClicked(CourseInfo item, int position) {
                    showInfoDialog(view.getId(), week, item.getCourseName(), item);
                }
            });
            _courseListDialog.show(this.getFragmentManager(), "x");
        }
    }

    private void showInfoDialog(int id, int week, String courseName, CourseInfo course) {
        selectedCourseNo = course.getCourseNo();
        AlertDialog.Builder course_dialog_builder = new AlertDialog.Builder(getActivity());
        course_dialog_builder.setTitle(courseName);
        String message = String.format(Locale.TAIWAN,
                "課號：%s\n時間：%s\n開課班級：%s",
                course.getCourseNo(),
                TIME_ARRAY[id - 1],
                course.getCourseClass()
        );
        course_dialog_builder.setMessage(message);
        course_dialog_builder.setPositiveButton("詳細內容", courseDetailDialogLis);
        course_dialog_builder.show();
    }

    @Override
    public void onTableInitialized(CourseTableLayout course_table) {
        fetchBuildingTable();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_classroom, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                Model.getInstance().deleteClassroomsInfo();
                fetchBuildingTable();
                break;
        }
        return true;
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_yellow;
    }

    @Override
    public int getTitleStringId() {
        return R.string.classroom_text;
    }

    @Override
    public void startCourseDetail(Object object) {
        if (object instanceof String) {
            showAlertMessage((String) object);
        } else if (object instanceof Boolean) {
            Intent i = new Intent(getActivity(),
                    CourseDetailActivity.class);
            i.putExtra("CourseNo", selectedCourseNo);
            startActivityForResult(i, 2);
        }
    }
}
