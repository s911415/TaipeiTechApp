TaipeiTechApp
====

這個App從一開始只有課表查詢並且在全體學生分享Apk給大家用，到後來增加wifi自動連線、學分計算功能和上架到Play Store，是作者大四時為學習開發Android App而做，這App上架近三年來累計了7338位使用者安裝，每次看到每一個學年度開始，使用者的增加幾乎是北科一屆學生的人數，還滿開心有這麼多人使用，也認識了其他平台(Web、iOS、WinPhone)的開發者，一起討論機制怎麼登入學校網站，算是作者大學時最後的作品。

作者經過一個洗澡的時間，思考自身是社會人士，App與這個專頁都是作者自己經營，上班之餘實在無力更新和維護，沒辦法全心全意照顧各位使用者及提供更便利的服務，所以決定終止這個App的服務。

這是我自己的最後一版，歡迎有興趣的人繼續開發下去。20161120

99 CSIE Alan Chiou

介紹
----

歡迎加入國立臺北科技大學！  
開始上課了，想查上課時間、地點、修課學分，這個App幫你到校園入口網站抓你想要的資訊，有任何想法都可以告訴我！

####App功能：

1. 行事曆  
更新、離線瀏覽該學年度行事曆

2. 課表查詢  
讓在校生查詢學生學期課表，並提供離線瀏覽，與Widget結合在桌面瀏覽

3. 教室查詢
可以查看教室的課堂

4. 學分計算  
個人歷年學分計算及畢業學分標準查詢，提供博雅修課狀況瀏覽，可調整課程類別模擬學分數


本App非官方授權，請自行斟酌。

已知的 Issue
----

1\. App本身在[AndroidManifest.xml](taipeiTech/src/main/AndroidManifest.xml)宣告了

`android:installLocation="auto"`

如果App被安裝到外部儲存空間，會造成部分功能無法使用，例如：課表Widget、活動報報的活動通知。

參考官方說明 https://developer.android.com/guide/topics/data/install-location.html#ShouldNot

相關連結
----

[GitHub AlanChiou/TaipeiTechApp](https://github.com/AlanChiou/TaipeiTechApp)
