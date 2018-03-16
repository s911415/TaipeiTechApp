package app.taipeitech.classroom.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import app.taipeitech.R;
import app.taipeitech.model.CourseInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseListDialog extends AppCompatDialogFragment {

    private static final String ITEMS = "items";

    private ArrayAdapter listAdapter;

    private ListView _listViewItems;

    private String _strTitle;

    private String _strPositiveButtonText;

    private DialogInterface.OnClickListener _onClickListener;

    private ClickableItem _clickableItem;

    public CourseListDialog() {

    }

    public static CourseListDialog newInstance(List<CourseInfo> items) {
        CourseListDialog multiSelectExpandableFragment = new
                CourseListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);

        multiSelectExpandableFragment.setArguments(args);

        return multiSelectExpandableFragment;
    }

    public void setOnItemClickListener(ClickableItem clickableItem) {
        this._clickableItem = clickableItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting the layout inflater to inflate the view in an alert dialog.
        LayoutInflater inflater = LayoutInflater.from(getActivity());


        // Crash on orientation change #7
        // Change Start
        // Description: As the instance was re initializing to null on rotating the device,
        // getting the instance from the saved instance
        if (null != savedInstanceState) {
            _clickableItem = (ClickableItem) savedInstanceState.getSerializable("item");
        }
        // Change End


        View rootView = inflater.inflate(R.layout.list_dialog, null);
        setData(rootView);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(rootView);

        String strPositiveButton = _strPositiveButtonText == null ? "CLOSE" : _strPositiveButtonText;
        alertDialog.setPositiveButton(strPositiveButton, _onClickListener);

        String strTitle = _strTitle == null ? "Select Item" : _strTitle;
        alertDialog.setTitle(strTitle);

        final AlertDialog dialog = alertDialog.create();
        return dialog;
    }

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }


    private void setData(View rootView) {
        List<CourseInfo> oldItems = (List<CourseInfo>) getArguments().getSerializable(ITEMS);
        List<CourseWrapper> items = new ArrayList<>();
        for (CourseInfo c : oldItems) {
            items.add(new CourseWrapper(c));
        }

        _listViewItems = (ListView) rootView.findViewById(R.id.listItems);

        //create the adapter by passing your ArrayList data
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, items);        //attach the adapter to the list
        _listViewItems.setAdapter(listAdapter);

        _listViewItems.setTextFilterEnabled(true);

        _listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _clickableItem.onItemClicked(((CourseWrapper) listAdapter.getItem(position)).info, position);
                getDialog().dismiss();
            }
        });
    }


    public interface ClickableItem extends Serializable {
        void onItemClicked(CourseInfo item, int position);
    }

    private class CourseWrapper {
        public final CourseInfo info;

        public CourseWrapper(CourseInfo i) {
            info = i;
        }

        public String toString() {
            return String.format("[%s] %s", info.getCourseNo(), info.getCourseClass());
        }
    }
}