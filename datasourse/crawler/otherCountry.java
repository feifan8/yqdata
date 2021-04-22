package getData.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import getData.bean.CovidBean;
import getData.bean.countryBean;
import getData.bean.provinceED;
import getData.util.HttpUtils;
import getData.util.TimeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class otherCountry {

    public void testCrawling() throws Exception {
        String html = HttpUtils.getHtml("https://ncov.dxy.cn/ncovh5/view/pneumonia");
        Document doc = (Document) Jsoup.parse(html);
        //System.out.println(html);

        //取国外数据
        String text = doc.select("script[id=getListByCountryTypeService2true]").toString();
        //正则表达式取json格式数据
        String pattern = "\\[(.*)\\]";//表达式
        Pattern reg = Pattern.compile(pattern);//编译正则对象
        Matcher matcher = reg.matcher(text);//与text匹配
        String jsonStr = "";
        if (matcher.find()) {
            jsonStr = matcher.group(0);
        } else {
            System.out.println("no match");
        }

        //连接数据库
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root");
        String sql = new String("replace into countrymap (countryFullName,confirmedCount) value(?,?)");

        //取出省份数据
        List<countryBean> cBeans = JSON.parseArray(jsonStr, countryBean.class);
        //存储
        for (countryBean cBean : cBeans) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cBean.getCountryFullName());
            ps.setInt(2, cBean.getConfirmedCount());
            ps.executeUpdate();
            ps.close();
        }
        conn.close();


    }
}
