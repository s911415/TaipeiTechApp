package app.taipeitech.utility;

import android.net.Uri;
import android.text.TextUtils;
import app.taipeitech.classroom.data.Classroom;
import app.taipeitech.course.data.Semester;
import app.taipeitech.model.CourseCollectionInfo;
import app.taipeitech.model.CourseInfo;
import app.taipeitech.model.StudentCourse;
import org.htmlcleaner.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseConnector {
    private static boolean isLogin = false;
    private static final String POST_COURSES_URI = "https://nportal.ntut.edu.tw/ssoIndex.do?apOu=aa_0010-&apUrl=http://aps.ntut.edu.tw/course/tw/courseSID.jsp";
    private static final String COURSES_URI = "http://aps.ntut.edu.tw/course/tw/courseSID.jsp";
    private static final String COURSE_URI = "http://aps.ntut.edu.tw/course/tw/Select.jsp";
    private static final String CLASSROOM_URI = "http://aps.ntut.edu.tw/course/tw/Croom.jsp";
    private static final String CLASSROOM_COURSE_URI = "http://aps.ntut.edu.tw/course/tw/Croom.jsp";

    public static String loginCourse() throws Exception {
        try {
            isLogin = false;
            String result = Connector.getDataByGet(POST_COURSES_URI, "utf-8", "https://nportal.ntut.edu.tw/aptreeList.do?apDn=ou=aa,ou=aproot,o=ldaproot");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] nodes = tagNode.getElementsByAttValue("name",
                    "sessionId", true, false);
            String sessionId = nodes[0].getAttributeByName("value");
            nodes = tagNode
                    .getElementsByAttValue("name", "userid", true, false);
            String userid = nodes[0].getAttributeByName("value");
            nodes = tagNode.getElementsByAttValue("name", "userType", true,
                    false);
            String userType = nodes[0].getAttributeByName("value");
            HashMap<String, String> courseParams = new HashMap<>();
            courseParams.put("sessionId", sessionId);
            courseParams.put("reqFrom", "Portal");
            courseParams.put("userid", userid);
            courseParams.put("userType", userType);
            result = Connector.getDataByPost(COURSES_URI, courseParams, "big5");
            isLogin = true;
            return result;
        } catch (Exception e) {
            NportalConnector.reset();
            e.printStackTrace();
            throw new Exception("登入課程系統時發生錯誤");
        }
    }

    public static ArrayList<Semester> getCourseSemesters(String sid)
            throws Exception {
        ArrayList<Semester> semesters = new ArrayList<>();
        String result;
        TagNode tagNode;
        try {
            if (!isLogin) {
                loginCourse();
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-3");
            params.put("code", sid);
            result = Connector.getDataByPost(COURSE_URI, params, "big5");
            tagNode = new HtmlCleaner().clean(result);
        } catch (Exception e) {
            isLogin = false;
            throw new Exception("學期資料讀取時發生錯誤");
        }
        if (result.contains("查無該學號的學生基本資料")) {
            throw new Exception("查無該學號的學生基本資料");
        }
        try {
            TagNode[] nodes = tagNode.getElementsByName("a", true);
            for (TagNode a : nodes) {
                String[] split = a.getText().toString().split(" ");
                semesters.add(new Semester(split[0], split[2]));
            }
        } catch (Exception e) {
            isLogin = false;
            throw new Exception("學期資料讀取時發生錯誤");
        }
        return semesters;
    }

    public static StudentCourse getStudentCourse(String sid, String year,
                                                 String semester) throws Exception {
        try {
            if (!isLogin) {
                loginCourse();
            }
            StudentCourse student = new StudentCourse();
            student.setSid(sid);
            student.setYear(year);
            student.setSemester(semester);
            ArrayList<CourseInfo> courseList = getCourses(sid, year, semester);
            student.setCourseList(courseList);
            student = Utility.cleanString(student);
            return student;
        } catch (Exception e) {
            isLogin = false;
            throw new Exception("課表讀取時發生錯誤");
        }
    }

    public static ArrayList<String> GetCourseDetail(String courseNo)
            throws Exception {
        try {
            if (!isLogin) {
                loginCourse();
            }
            ArrayList<String> courseDetail = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-1");
            params.put("code", courseNo);
            String result = Connector.getDataByPost(COURSE_URI, params, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
                    true, false);

            TagNode[] rows = tables[0].getElementsByName("tr", true);
            for (TagNode row : rows) {
                TagNode[] cols = row.getElementsByName("th", true);
                StringBuilder sb = new StringBuilder();
                String title = cleanUpString(cols[0].getText().toString());
                String content = null;

                sb.append(title);
                cols = row.getElementsByName("td", true);
                TagNode[] links = cols[0].getElementsByName("a", false);

                if (links.length > 0) {
                    ArrayList<String> linksContent = new ArrayList<>();
                    for (TagNode a : links)
                        linksContent.add(cleanUpString(a.getText().toString()));

                    int lastIndex = linksContent.size() - 1;
                    if (title.equals("授課教師") && linksContent.get(lastIndex).startsWith("《查詢")) {
                        linksContent.remove(lastIndex);
                    }

                    content = TextUtils.join("/", linksContent);
                } else {
                    content = cleanUpString(cols[0].getText().toString());
                }

                if (title.equals("類別") && content != null) {
                    content = mappingCourseType(content) + " " + content;
                }

                if (content != null) {
                    sb.append("：");
                    sb.append(content);
                }

                courseDetail.add(sb.toString());
            }
            return courseDetail;
        } catch (Exception e) {
            isLogin = false;
            throw new Exception("課程資訊讀取時發生錯誤");
        }
    }

    private static String mappingCourseType(String str) {
        switch (str) {
            case "○":
                return "必 (共同)";
            case "△":
                return "必 (共同)";
            case "☆":
                return "選 (共同)";
            case "●":
                return "必 (專業)";
            case "▲":
                return "必 (專業)";
            case "★":
                return "選 (專業)";
        }

        return "";
    }

    private static String cleanUpString(String str) {
        return Utility.cleanString(str).replaceAll("\\s", "").trim();
    }

    public static ArrayList<String> GetClassmate(String courseNo)
            throws Exception {
        ArrayList<String> classmates = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("format", "-1");
        params.put("code", courseNo);
        String result;
        try {
            result = Connector.getDataByPost(COURSE_URI, params, "big5");
        } catch (Exception e) {
            throw new Exception("學生名單讀取時發生錯誤");
        }
        TagNode tagNode;
        tagNode = new HtmlCleaner().clean(result);
        TagNode[] tables = tagNode.getElementsByAttValue("border", "1", true,
                false);

        TagNode[] rows = tables[1].getElementsByName("tr", true);
        for (int i = 1; i < rows.length; i++) {
            TagNode[] cols = rows[i].getElementsByName("td", true);
            String d = cols[0].getText().toString();
            d = d + "," + cols[1].getText().toString();
            d = d + "," + cols[2].getText().toString();
            d = d.replace("　", "");
            d = d.replace("\n", "");
            classmates.add(d);
        }
        return classmates;
    }

    public static String getCourseType(String courseNo) throws Exception {
        try {
            if (!isLogin) {
                loginCourse();
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-1");
            params.put("code", courseNo);
            String result = Connector.getDataByPost(COURSE_URI, params, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
                    true, false);

            TagNode[] rows = tables[0].getElementsByName("tr", true);
            TagNode[] temp = rows[7].getElementsByName("td", true);
            return temp[0].getText().toString();
        } catch (Exception ex) {
            throw new Exception("課程類別讀取時發生錯誤");
        }
    }

    private static ArrayList<CourseInfo> getCourses(String sid, String year,
                                                    String semester) throws Exception {
        if (!isLogin) {
            loginCourse();
        }
        ArrayList<CourseInfo> courses = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("format", "-2");
        params.put("code", sid);
        params.put("year", year);
        params.put("sem", semester);
        String result = Connector.getDataByPost(COURSE_URI, params, "big5");
        params.put("format", "-4");
        String resultTable = Connector.getDataByPost(COURSE_URI, params, "big5");
        TagNode tagNode = new HtmlCleaner().clean(result);
        TagNode tagNode2 = new HtmlCleaner().clean(resultTable);
        TagNode[] nodes = tagNode.getElementsByAttValue("border", "1", true,
                false);
        TagNode[] rows = nodes[0].getElementsByName("tr", true);
        for (int i = 3; i < rows.length - 1; i++) {
            TagNode[] cols = rows[i].getElementsByName("td", true);
            CourseInfo course = new CourseInfo();
            TagNode[] a = cols[0].getElementsByName("a", true);
            if (a.length == 0) {
                course.setCourseNo("0");
            } else {
                course.setCourseNo(a[0].getText().toString());
            }
            course.setCourseName(cols[1].getText().toString());
            course.setCourseTeacher(cols[6].getText().toString());
            course.setCourseTime(new String[]{
                    cols[8].getText().toString().trim(),
                    cols[9].getText().toString().trim(),
                    cols[10].getText().toString().trim(),
                    cols[11].getText().toString().trim(),
                    cols[12].getText().toString().trim(),
                    cols[13].getText().toString().trim(),
                    cols[14].getText().toString().trim()
            });
            updateClassRoom(course, tagNode2);
            courses.add(course);
        }
        return courses;
    }

    private static void updateClassRoom(CourseInfo course, TagNode tagNode) {
        final String[] courseTimes = course.getCourseTimes();
        String[] courseRooms = new String[courseTimes.length];
        TagNode[] rows = tagNode.getElementsByName("tr", true);
        final int START_ROW = 2;
        boolean hasWeekend = rows[1].getAllChildren().size() == 8;
        for (int i = 0; i < courseTimes.length; i++) {
            courseRooms[i] = "";
            if (!hasWeekend && (i == 0 || i == 6)) continue;
            try {
                String[] courseTimeArr = courseTimes[i].trim().split("\\s");
                if (courseTimeArr.length == 0) continue;
                int firstSection = Integer.parseInt(courseTimeArr[0], 16);
                int col = hasWeekend ? i + 1 : i;
                TagNode td = rows[firstSection + START_ROW - 1].getChildTagList().get(col);
                TagNode[] links = td.getElementsByName("a", false);
                if (links.length > 0) {
                    TagNode lastNode = links[links.length - 1];
                    String href = lastNode.getAttributeByName("href");
                    if (href != null && href.startsWith("Croom.jsp")) {
                        courseRooms[i] = lastNode.getText().toString().trim();
                    }
                }

            } catch (NumberFormatException ignored) {
            }
        }
        course.setCourseRooms(courseRooms);
    }


    public static List<Classroom> getClassrooms(String year, String semester) throws Exception {
        List<Classroom> ret = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("format", "-2");
        params.put("year", year);
        params.put("sem", semester);
        String result = Connector.getDataByPost(CLASSROOM_URI, params, "big5");
        TagNode tagNode = new HtmlCleaner().clean(result);
        TagNode[] nodes = tagNode.getElementsByName("tr", true);
        for (TagNode tr : nodes) {
            TagNode[] cells = tr.getAllElements(false);
            if (cells.length == 9) {
                TagNode[] childElems = cells[0].getAllElements(false);
                if (childElems.length > 0) {
                    final TagNode firstNode = childElems[0];
                    String href = firstNode.getAttributeByName("href");
                    if (firstNode.getName().equals("a") && href != null && href.startsWith("Croom.jsp")) {
                        Uri uri = Uri.parse(href);
                        String code = uri.getQueryParameter("code");
                        if (code != null && !code.isEmpty()) {
                            ret.add(new Classroom(code, Utility.cleanString(firstNode.getText().toString())));
                        }
                    }
                }
            }
        }
        Collections.sort(ret, Classroom.comparator);
        return ret;
    }

    public static StudentCourse getClassroomCourses(String year, String semester, String code) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("format", "-3");
        params.put("year", year);
        params.put("sem", semester);
        params.put("code", code);
        String result = Connector.getDataByPost(CLASSROOM_COURSE_URI, params, "big5");
        final TagNode tagNode = new HtmlCleaner().clean(result);
        TagNode[] tables = tagNode.getElementsByName("table", true);
        if (tables.length == 2) {
            final TagNode coursesTable = tables[1];
            final TagNode[] rows = coursesTable.getElementsByName("tr", true);
            final TagNode[][] courseTableArray = new TagNode[13][7];
            HashMap<String, CourseInfo> courseMap = new HashMap<>();
            ArrayList<HtmlNode> _tmpNodes = new ArrayList<>();
            Pattern sectionNoPattern = Pattern.compile("^\\(([0-9A-Za-z]+)\\)");
            int rowIdx = 0;
            for (final TagNode row : rows) {
                final TagNode[] cells = row.getAllElements(false);
                if (cells.length == 0 || !"center".equals(cells[0].getAttributeByName("align"))) continue;

                if (cells.length == 8) {// From Sun to Sat
                    System.arraycopy(cells, 1, courseTableArray[rowIdx], 0, 7);
                    rowIdx++;

                } else if (cells.length == 6) { // From Mon to Fri
                    System.arraycopy(cells, 1, courseTableArray[rowIdx], 1, 5);
                    rowIdx++;
                }

                for (int week = 0; week < 7; week++) {
                    for (int section = 0; section < 13; section++) {
                        TagNode cell = courseTableArray[section][week];
                        if (cell == null) continue;
                        _tmpNodes.clear();

                        for (BaseToken baseToken : cell.getAllChildren()) {
                            if (baseToken instanceof TagNode) {
                                final String tagName = ((TagNode) baseToken).getName();

                                if (!tagName.equals("br")) {
                                    _tmpNodes.add((TagNode) baseToken);
                                }
                            } else if (baseToken instanceof ContentNode) {
                                if (!((ContentNode) baseToken).getContent().trim().isEmpty()) {
                                    _tmpNodes.add((ContentNode) baseToken);
                                }
                            }
                        }

                        if (week == 2 && section == 4) {
                            section *= 1;
                        }

                        for (int j = 0, k = _tmpNodes.size(); j < k; j++) {
                            final HtmlNode node = _tmpNodes.get(j);
                            if (node instanceof TagNode) {
                                final TagNode tNode = (TagNode) node;
                                if (tNode.getAttributeByName("href").startsWith("Curr.jsp") && _tmpNodes.get(j - 1) instanceof ContentNode) {
                                    ContentNode contentNode = (ContentNode) _tmpNodes.get(j - 1);
                                    Matcher m = sectionNoPattern.matcher(contentNode.getContent().trim());
                                    if (m.find()) {
                                        String sectionNo = m.group(1);
                                        CourseInfo cInfo = courseMap.get(sectionNo);
                                        String[] courseTime = null;
                                        if (cInfo == null) {
                                            cInfo = new CourseInfo();
                                            courseMap.put(sectionNo, cInfo);
                                            cInfo.setCourseNo(sectionNo);
                                            cInfo.setCourseName(tNode.getText().toString().trim());
                                            courseTime = new String[7];
                                            for (int u = 0; u < courseTime.length; u++) {
                                                courseTime[u] = "";
                                            }
                                            cInfo.setCourseTime(courseTime);
                                        } else {
                                            courseTime = cInfo.getCourseTimes();
                                        }
                                        HashSet<String> tmpSet = new HashSet<>(Arrays.asList(courseTime[week].split(" ")));
                                        tmpSet.add(String.valueOf(section + 1));
                                        courseTime[week] = TextUtils.join(" ", tmpSet);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ArrayList<CourseInfo> tmpRet = new ArrayList<>(courseMap.values());
            ArrayList<CourseInfo> ret = new ArrayList<>();
            for (int i = 0, k = tmpRet.size(); i < k - 1; i++) {
                CourseCollectionInfo collectionInfo = new CourseCollectionInfo();
                final CourseInfo iCourse = tmpRet.get(i);
                collectionInfo.addCourseInfo(iCourse);
                for (int j = i + 1; j < k; j++) {
                    final CourseInfo jCourse = tmpRet.get(j);
                    if (Utility.isArrayAreSame(iCourse.getCourseTimes(), jCourse.getCourseTimes())) {
                        collectionInfo.addCourseInfo(jCourse);
                    }
                }

                ret.add(collectionInfo);
            }


            StudentCourse student = new StudentCourse();
            student.setSid("0");
            student.setYear(year);
            student.setSemester(semester);
            student.setCourseList(ret);
            return student;
        } else {
            throw new Exception("Format changed");
        }
    }


    public static boolean isLogin() {
        return isLogin;
    }
}
