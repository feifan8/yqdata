package getData.crawler;

import getData.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class rumorCrawler {
    public void testCrawling() throws Exception {
        //获取谣言
        String html = HttpUtils.getHtml("https://vp.fact.qq.com/home");
        Document doc = (Document) Jsoup.parse(html);
        String rumortext = doc.select("div[class=content_text]").toString();
        String datetext = doc.select("div[class=content_time]").toString();
        //提取谣言文本
        Matcher rumorMatcher = Pattern.compile("content_text\">(.*?)&", Pattern.DOTALL).matcher(rumortext);
        Matcher markMatcher = Pattern.compile("mark\">(.*?)</span>", Pattern.DOTALL).matcher(rumortext);
        Matcher dateMatcher = Pattern.compile("content_time\">(.*?)</div>", Pattern.DOTALL).matcher(datetext);

        //存储谣言文本
        List<String> list1 = new ArrayList<>();//谣言id
        List<String> list2 = new ArrayList<>();//谣言
        List<String> list3 = new ArrayList<>();//真假
        List<String> list4 = new ArrayList<>();//日期
        int i=1;
        while(rumorMatcher.find()){
            String s = Integer.toString(i);
            list1.add(s);
            list2.add(rumorMatcher.group(1));
            i++;
        }
        while(markMatcher.find()){
            list3.add(markMatcher.group(1));
        }
        while(dateMatcher.find()){
            list4.add(dateMatcher.group(1));
        }

        //连接数据库
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root");
        String sql = new String("replace into rumor (rid,rumor,mark,date) value(?,?,?,?)");
        //存储
        for(int j=0;j<list1.size();j++){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, list1.get(j));
            ps.setString(2, list2.get(j));
            ps.setString(3, list3.get(j));
            ps.setString(4, list4.get(j));
            ps.executeUpdate();
            ps.close();
        }
        conn.close();
        String str = "存储到数据库";
        System.out.println(str);

    }
}
