package app.taipeitech.classroom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import app.taipeitech.classroom.data.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s911415
 */
public class BuildingSelector extends AppCompatButton implements View.OnClickListener {
    private OnBuildingSelectedListener mOnBuildingSelectedListener;
    private ArrayList<Building> mBuildingList = new ArrayList<>();
    private String[] mBuildingArray;

    public BuildingSelector(Context context, AttributeSet attrs) {
        super(context, attrs, android.support.v7.appcompat.R.attr.spinnerStyle);
    }

    public BuildingSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.setOnClickListener(this);
    }

    public void setOnBuildingSelectedListener(OnBuildingSelectedListener listener) {
        mOnBuildingSelectedListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }

    public void setBuildingList(List<Building> list) {
        mBuildingList.clear();
        mBuildingList.addAll(list);
        mBuildingArray = new String[list.size()];
        for (int i = list.size() - 1; i >= 0; i--) {
            mBuildingArray[i] = list.get(i).getBuildingName();
        }
    }

    public void setText(Building building) {
        if (building != null) {
            setText(building.getBuildingName());
        } else {
            setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if (mBuildingArray == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("大樓");
        builder.setItems(mBuildingArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setText(mBuildingList.get(which));
                if (mOnBuildingSelectedListener != null) {
                    mOnBuildingSelectedListener.onBuildingSelected(mBuildingList.get(which));
                }
            }
        });
        builder.show();
    }

    public interface OnBuildingSelectedListener {
        void onBuildingSelected(Building building);
    }
}
