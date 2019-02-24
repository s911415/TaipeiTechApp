package app.taipeitech.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import app.taipeitech.R;
import app.taipeitech.model.EventInfo;
import app.taipeitech.utility.Utility;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarListAdapter extends ArrayAdapter<EventInfo> implements
        OnClickListener, DialogInterface.OnClickListener {
    private static final int LAYOUT_ID = R.layout.calendar_item;
    private LayoutInflater inflater;
    private EventInfo selectedEvent;
    private static final String DATETIME_FORMAT = "yyyy/MM/dd (E)";

    public CalendarListAdapter(Context context, List<EventInfo> objects) {
        super(context, LAYOUT_ID, objects);
        // tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(LAYOUT_ID, parent, false);
            holder = new ViewHolder();
            holder.date_textview = (TextView) convertView
                    .findViewById(R.id.calendarDate);
            holder.day_textview = (TextView) convertView
                    .findViewById(R.id.calendarDay);
            holder.event_textview = (TextView) convertView
                    .findViewById(R.id.calendarEvent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EventInfo event = getItem(position);
        Calendar startDateCal = event.getStartDateCal();
        holder.date_textview.setText(Utility.getDate(startDateCal));
        holder.day_textview.setText(Utility.getDateString("E", startDateCal));
        holder.event_textview.setText(event.getEvent());

        convertView.setTag(R.id.data_tag, event);
        convertView.setOnClickListener(this);
        return convertView;
    }

    private class ViewHolder {
        public TextView date_textview;
        public TextView day_textview;
        public TextView event_textview;
    }

    @Override
    public void onClick(View v) {
        selectedEvent = (EventInfo) v.getTag(R.id.data_tag);
        Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("活動內容");
        String message = null;
        if (selectedEvent.isOneDayEvent()) {
            message = String.format(
                    Locale.TAIWAN,
                    "%s\n\n時間：%s",
                    selectedEvent.getEvent(),
                    Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getStartDateCal()));
        } else {
            message = String.format(
                    Locale.TAIWAN,
                    "%s\n\n開始時間：%s\n結束時間：%s",
                    selectedEvent.getEvent(),
                    Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getStartDateCal()),
                    Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getEndDateCal()));
        }

        builder.setMessage(message);
        builder.setNegativeButton(R.string.add_to_calendar, this);
        builder.setPositiveButton(R.string.share_using, this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        switch (position) {
            case DialogInterface.BUTTON_NEGATIVE:
                try {
                    Intent calendarIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    calendarIntent.setData(CalendarContract.Events.CONTENT_URI);

                    calendarIntent.setType("vnd.android.cursor.item/event");

                    calendarIntent.putExtra(CalendarContract.Events.DTSTART, selectedEvent
                            .getStartDateCal().getTimeInMillis());
                    calendarIntent.putExtra(CalendarContract.Events.DTEND,
                            selectedEvent.getEndDateCal().getTimeInMillis());
                    calendarIntent.putExtra(CalendarContract.Events.EVENT_TIMEZONE,
                            selectedEvent.getStartDateCal().getTimeZone().getID());

                    calendarIntent.putExtra(CalendarContract.Events.ALL_DAY, true);

                    calendarIntent.putExtra(CalendarContract.Events.TITLE, selectedEvent.getEvent());

                    getContext().startActivity(calendarIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.calendar_not_support,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case DialogInterface.BUTTON_POSITIVE:
                String shareBody = null;
                if (selectedEvent.isOneDayEvent()) {
                    shareBody = Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getStartDateCal())
                            + " "
                            + selectedEvent.getEvent();
                } else {
                    shareBody = Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getStartDateCal())
                            + "~"
                            + Utility.getDateString(DATETIME_FORMAT,
                            selectedEvent.getEndDateCal())
                            + " "
                            + selectedEvent.getEvent();
                }
                Intent sharing_intent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharing_intent.setType("text/plain");
                sharing_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharing_intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        shareBody);
                getContext().startActivity(
                        Intent.createChooser(sharing_intent, getContext()
                                .getResources().getString(R.string.share_using)));
                break;
        }
    }
}
