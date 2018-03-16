package app.taipeitech.classroom.component;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import app.taipeitech.R;
import app.taipeitech.classroom.data.Classroom;

import java.util.ArrayList;
import java.util.List;

public class ClassroomSpinner extends AppCompatSpinner implements View.OnTouchListener, ClassroomListDialog.SearchableItem<Classroom> {
    private Context _context;
    private List<Classroom> _items, _dataset;
    private ClassroomListDialog _searchableListDialog;

    private boolean _isDirty;
    private ClassroomAdapter _arrayAdapter;
    private ClassroomListDialog.SearchableItem<Classroom> _selected = null;

    public ClassroomSpinner(Context context) {
        super(context);
        this._context = context;
        init();
    }

    public ClassroomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        init();
    }

    public ClassroomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        init();
    }

    private void init() {
        _items = new ArrayList<>();
        _dataset = new ArrayList<>();
        _searchableListDialog = ClassroomListDialog.newInstance(_items);
        _searchableListDialog.setOnSearchableItemClickListener(this);
        _searchableListDialog.setTitle(getContext().getResources().getString(R.string.no_classroom_selected));
        _searchableListDialog.setPositiveButton(getContext().getResources().getString(R.string.close_drawer));
        setOnTouchListener(this);
    }

    public void setDataSet(List<Classroom> data) {
        _dataset.clear();
        this.setSelection(0);
        _dataset.addAll(data);
        _arrayAdapter = (ClassroomAdapter) getAdapter();
        if (_arrayAdapter == null) {
            ClassroomAdapter arrayAdapter = new ClassroomAdapter(_context, android.R.layout.simple_list_item_1, _dataset);
            setAdapter(arrayAdapter);
            _arrayAdapter = arrayAdapter;
        }

        _arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (null != _arrayAdapter) {
                _items.clear();
                for (int i = 0; i < _arrayAdapter.getCount(); i++) {
                    _items.add((Classroom) _arrayAdapter.getItem(i));
                }
                // Change end.

                _searchableListDialog.show(scanForActivity(_context).getFragmentManager(), "TAG");
            }
        }
        return true;
    }

    //The method just below is executed  when an item in the searchlist is tapped.This is where we store the value int string called selectedItem.
    @Override
    public void onSearchableItemClicked(Classroom item, int position) {
        final int idx = _items.indexOf(item);
        setSelection(idx);

        if (!_isDirty) {
            _isDirty = true;
            setAdapter(_arrayAdapter);
            setSelection(idx);
        }

        if (_selected != null) {
            _selected.onSearchableItemClicked(item, position);
        }
    }

    public void setOnItemSelected(ClassroomListDialog.SearchableItem<Classroom> c) {
        this._selected = c;
    }


    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    @Override
    public Classroom getSelectedItem() {
        return (Classroom) super.getSelectedItem();
    }
}