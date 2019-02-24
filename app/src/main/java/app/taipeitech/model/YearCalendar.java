package app.taipeitech.model;

import app.taipeitech.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YearCalendar {
    private List<EventInfo> eventList = null;
    private String semester = null;

    public YearCalendar() {
    }

    public YearCalendar(String semester, List<EventInfo> eventInfoList) {
        this.semester = semester;
        this.eventList = eventInfoList;
    }

    public List<EventInfo> getEventList() {
        return eventList;
    }

    public void setEventList(List<EventInfo> eventList) {
        this.eventList = eventList;
    }

    public int getYear() {
        return Integer.parseInt(semester) + 1911;
    }

    public String getSemesterYear() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<EventInfo> searchEventList(String keyword) {
        List<EventInfo> resultList = new ArrayList<>();
        if (eventList != null && keyword != null) {
            for (EventInfo eventInfo : eventList) {
                if (eventInfo.getEvent().contains(keyword)) {
                    resultList.add(eventInfo);
                }
            }
        }
        return resultList;
    }

    public List<EventInfo> getMonthEventList(String year, String month) {
        List<EventInfo> resultList = new ArrayList<>();
        if (eventList != null && month != null) {
            for (EventInfo eventInfo : eventList) {
                if (Utility.getMonth(eventInfo.getStartDateCal()).equals(month)
                        && Utility.getYear(eventInfo.getStartDateCal()).equals(
                        year) && eventInfo.getId() != null) {
                    resultList.add(eventInfo);
                }
            }
        }
        return resultList;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month + 1, day, 0, 0, 0);
        Date date = cal.getTime();
        return date;
    }

    public static Date getDate(String date_string) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
        Date date = sdf.parse(date_string);
        return date;
    }

    public List<String> findEvents(Date date) {
        List<String> resultList = new ArrayList<>();
        if (eventList != null && date != null) {
            for (EventInfo eventInfo : eventList) {
                if (eventInfo.getStartDateCal().getTime().equals(date)) {
                    resultList.add(eventInfo.getEvent());
                }
            }
        }
        return resultList;
    }
}
