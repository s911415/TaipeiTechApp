package app.taipeitech.model;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventInfo implements Comparable<EventInfo> {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Taipei");
    private final Integer id;
    private final Long calStart;
    private final Long calEnd;
    private final String allDay;
    private final String calTitle;
    private final String calPlace;
    private final String calContent;
    private final String calColor;
    private final String ownerId;
    private final String ownerName;
    private final String creatorId;
    private final String creatorName;
    private final Long modifyDate;
    private final Integer hasBeenDeleted;
    private final List<Object> calInviteeList;
    private final List<Object> calAlertList;

    private Calendar startDateCal;
    private Calendar endDateCal;

    public EventInfo(
            Integer id, Long calStart, Long calEnd,
            String allDay,
            String calTitle, String calPlace, String calContent,
            String calColor, String ownerId, String ownerName,
            String creatorId, String creatorName,
            Long modifyDate,
            Integer hasBeenDeleted,
            List<Object> calInviteeList, List<Object> calAlertList
    ) {
        this.id = id;
        this.calStart = calStart;
        this.calEnd = calEnd;
        this.allDay = allDay;
        this.calTitle = calTitle;
        this.calPlace = calPlace;
        this.calContent = calContent;
        this.calColor = calColor;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.modifyDate = modifyDate;
        this.hasBeenDeleted = hasBeenDeleted;
        this.calInviteeList = calInviteeList;
        this.calAlertList = calAlertList;
    }

    public Integer getId() {
        return id;
    }

    public String getEvent() {
        return calTitle;
    }

    private Long getCalEndTime() {
        return calEnd - 1;
    }

    public Calendar getStartDateCal() {
        if (startDateCal == null)
            this.startDateCal = parseUnixTimestamp(calStart);

        return startDateCal;
    }

    public Calendar getEndDateCal() {
        if (this.endDateCal == null)
            this.endDateCal = parseUnixTimestamp(getCalEndTime());

        return endDateCal;
    }

    public boolean isOneDayEvent() {
        return calEnd - calStart == 86400000;
    }

    private static Calendar parseUnixTimestamp(Long timestamp) {
        Calendar cal = Calendar.getInstance(TIME_ZONE);
        cal.setTime(new Date(timestamp));
        return cal;
    }

    @Override
    public int compareTo(@NonNull EventInfo o) {
        int startCmp = calStart.compareTo(o.calStart);
        if (startCmp != 0) return startCmp;
        return calEnd.compareTo(o.calEnd);
    }
}
