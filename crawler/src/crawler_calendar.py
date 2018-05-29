# Reference : https://github.com/kamisakihideyoshi/TaipeiTechRefined
import re
import sys
import urllib.request
import urllib.request

from bs4 import BeautifulSoup


def format_date(date_str):
    date_arr = date_str.split("/")
    return '%02d-%02d' % (int(date_arr[0]), int(date_arr[1]))


def process_string_to_date(data_str, prev_date=None):
    if data_str is None:
        return None

    return_date = ""

    # 如果只有日期的話
    if re.match("^\d{1,2}$", data_str):
        if (prev_date is None):
            sys.stderr.write("Error when parsing: " + data_str)
        else:
            return_date = prev_date.split("/")[0] + "/" + data_str
    # 如果日期是 3 個數字連一起 EX.810，將它分開 (行政人員輸入問題)
    # 然後將endDate轉型成str
    elif re.match("\d{3,4}", data_str):
        return_date = str(int(data_str) // 100) + "/" + str(
            int(data_str) % 100)
    else:
        return_date = data_str

    return return_date


def main():
    url = "https://oaa.ntut.edu.tw/files/13-1001-77326.php?Lang=zh-tw"
    hdr = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; Win64; x64)'}
    req = urllib.request.Request(url, headers=hdr)
    html = response = urllib.request.urlopen(req)
    dataList = []
    soup = BeautifulSoup(html.read(), "html.parser")

    # 所有 <P> 的清單
    infoList = soup.findAll({'td'})

    for info in infoList:
        # 尋找包含日期的資料
        for data in re.findall("\(\d*/\d*.*|\(\d+/*\d*-*\d+/*\d*\).*",
                               info.text):
            try:
                # 將日期前的'('取代成' ('
                # 然後用' '或'、 '分割開來加入 dataList
                dataMatch = re.split("、\s*(?=\()|(?<=\S)\s+(?=\()",
                                     re.sub("(?<=\S)\((?=\d*/\d*\))", " (", data))
                for dat in dataMatch:
                    if isinstance(dat, str):
                        dat = dat.strip()
                        if dat not in dataList:
                            dataList.append(dat)
            except:
                pass
    # print(dataList)

    # 上面是爬蟲下面是 JSON
    # 建立 JSON 格式

    semester = 'N/A'

    # 從所有清單中尋找當前學年度，並寫入 semester
    for lookFor in dataList:
        if re.search("\d*(?=學年度第.學期開始)", lookFor):
            semester = int(re.search("\d*(?=學年度第.學期開始)", lookFor).group())

    tmp = r'{"semester":"%s","eventList":[' % (semester)

    # 找出開始與結束日期
    for data in dataList:
        startDate = re.search("(\d+/*\d+)", data)
        endDate = re.search("(?<=-)\d*((/\d*)?)", data)

        # 如果日期是3個數字連一起 EX.810，將它分開 (行政人員輸入問題)
        # 然後將 startDate 轉型成 str
        startDate = process_string_to_date(startDate.group())
        endDate = process_string_to_date(None if endDate is None else endDate.group(), startDate)

        # 找出內文的部分
        event = re.search("\d*[\u4e00-\u9fa5]+.*", data)

        # 依照開始月份判斷是學期開始當年 OR 隔年
        if re.match(r"[0-7]/(?=\d*)", startDate):
            semester_end = semester_start = int(semester) + 1912
            flag = 0
        else:
            semester_end = semester_start = int(semester) + 1911
            flag = 1

        startDate = format_date(startDate)
        # 沒有結束日期的話直接寫入 JSON
        if endDate is None:
            tmp += '{"startDate":"%s/%s","endDate":"%s/%s","event":"%s"},' % (
                semester_start, startDate, semester_start, startDate,
                event.group().strip())
        # 有結束日期的話，判斷一下在寫入 JSON
        else:
            # 如果是在1~7月且開始月份不再 1~7 月，年份 + 1
            if re.match(r"[0-7]/(?=\d*)", endDate) and flag:
                semester_end += 1

            endDate = format_date(endDate)
            tmp += '{"startDate": "%s/%s","endDate":"%s/%s","event":"%s"},' % (
                semester_start, startDate, semester_end, endDate, event.group())

    # 補完 JSON 檔
    tmp = tmp.rstrip(',')
    tmp += ']}'
    tmp = tmp.replace('/', '-')

    # 輸出
    with open('../out/calendar.json', 'w', encoding='utf8') as f:
        f.write(tmp)
    # print(tmp)


if __name__ == '__main__':
    main()
