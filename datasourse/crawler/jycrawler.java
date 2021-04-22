package getData.crawler;

import com.alibaba.fastjson.JSON;
import getData.bean.CovidBean;
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

public class jycrawler {
    public void testCrawling() throws Exception {
        String datetime = TimeUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
        String html = HttpUtils.getHtml("https://ncov.dxy.cn/ncovh5/view/pneumonia");

        Document doc = (Document) Jsoup.parse(html);
        //取地区数据
        String text = doc.select("script[id=getAreaStat]").toString();

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

        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8", "root", "root");
        String sql = new String("replace into fromothercountry (provinceShortName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,dateTime) value(?,?,?,?,?,?,?)");

        //取出省份数据
        List<CovidBean> pCovidBeans = JSON.parseArray(jsonStr, CovidBean.class);
        for (CovidBean pBean : pCovidBeans) {
            pBean.setDateTime(datetime);
            String citysStr = pBean.getCities();

            //取出省份下各城市数据
            List<CovidBean> covidBeans = JSON.parseArray(citysStr, CovidBean.class);
            for (CovidBean bean : covidBeans) {
                if(bean.getCityName().indexOf("境外输入")==1){
                    bean.setProvinceShortName(pBean.getProvinceShortName());
                    bean.setDateTime(datetime);

                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, bean.getProvinceShortName());
                    ps.setLong(2, bean.getCurrentConfirmedCount());
                    ps.setLong(3, bean.getConfirmedCount());
                    ps.setLong(4, bean.getSuspectedCount());
                    ps.setLong(5, bean.getCuredCount());
                    ps.setLong(6, bean.getDeadCount());
                    ps.setString(7, bean.getDateTime());
                    ps.executeUpdate();
                    ps.close();
                }
            }
        }
        conn.close();
        System.out.println("存储完毕");
    }
}
