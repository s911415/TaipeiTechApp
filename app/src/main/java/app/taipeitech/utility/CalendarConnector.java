package app.taipeitech.utility;

import app.taipeitech.model.EventInfo;
import app.taipeitech.model.YearCalendar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class CalendarConnector {
    private final static String CALENDAR_URI = "https://raw.githubusercontent.com/s911415/TaipeiTechApp/master/crawler/out/calendar.json";
    private final static String APP_CALENDAR_URI = "https://nportal.ntut.edu.tw/calModeApp.do";

    public static YearCalendar getEventList() throws Exception {
        try {
            int currentSem = getCurrentSemester();
            String startDate = (currentSem + 1911 - 1) + "/01/01";
            String endDate = (currentSem + 1911 + 2) + "/08/01";
            Map<String, String> postData = new HashMap<>();
            postData.put("startDate", startDate);
            postData.put("endDate", endDate);
            String result = Connector.getDataByPost(APP_CALENDAR_URI, postData, "utf-8");

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            EventInfo[] eventInfos = gson.fromJson(result, EventInfo[].class);

            List<EventInfo> eventInfoArrayList = new ArrayList<>();
            for (EventInfo e : eventInfos) {
                if (e.getId() != null) eventInfoArrayList.add(e);
            }

            YearCalendar yearCalendar = new YearCalendar(String.valueOf(currentSem), eventInfoArrayList);
            return yearCalendar;
        } catch (Exception e) {
            NportalConnector.reset();
            e.printStackTrace();
            throw new Exception("行事曆讀取時發生錯誤");
        }
    }

    private static int getCurrentSemester() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int sem = year - 1911;
        if (month < 8) sem--;

        return sem;
    }
}
