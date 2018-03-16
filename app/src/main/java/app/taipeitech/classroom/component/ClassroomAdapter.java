package app.taipeitech.classroom.component;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import app.taipeitech.classroom.data.Classroom;

import java.util.List;

public class ClassroomAdapter extends ArrayAdapter<Classroom> {
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ClassroomAdapter(@NonNull Context context, @LayoutRes int resource,
                            @NonNull List<Classroom> objects) {
        super(context, resource, 0, objects);
    }

}
