package getData.crawler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import getData.bean.CovidBean;
import getData.bean.provinceED;
import getData.saveData.saveUtils;
import getData.util.HttpUtils;
import getData.util.TimeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Component
public class MyCrawler {
    //每5s执行一次
    //@Scheduled(cron = "0/5 * * * * ?")

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

        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root");
        String sql = new String("replace into city (provinceName,provinceShortName,cityName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,locationId,pid,cities,dateTime,citydate) value(?,?,?,?,?,?,?,?,?,?,?,?,?)");

        //取出省份数据
        List<CovidBean> pCovidBeans = JSON.parseArray(jsonStr, CovidBean.class);
        for (CovidBean pBean : pCovidBeans) {
            pBean.setDateTime(datetime);
            String citysStr = pBean.getCities();

            //取出省份下各城市数据
            List<CovidBean> covidBeans = JSON.parseArray(citysStr, CovidBean.class);
            for (CovidBean bean : covidBeans) {
                bean.setProvinceName(pBean.getProvinceName());
                bean.setProvinceShortName(pBean.getProvinceShortName());
                bean.setDateTime(datetime);
                bean.setPid(pBean.getLocationId());

                //saveUtils.saveData(bean);

            }

            //取出各省份20年到21年的每一天的数据
            String statisticDataUrl = pBean.getStatisticsData();
            String statisticData = HttpUtils.getHtml(statisticDataUrl);
            JSONObject jsonObject = JSON.parseObject(statisticData);
            String dataStr = jsonObject.getString("data");
            String pattern2 = "\\[(.*)\\]";//表达式
            Pattern reg2 = Pattern.compile(pattern2);//编译正则对象
            Matcher matcher2 = reg2.matcher(dataStr);//与text匹配
            String jsonStr2 = "";
            if (matcher2.find()) {
                jsonStr2 = matcher2.group(0);
            } else {
                System.out.println("no match");
            }

            //各省份每一天的数据
            List<provinceED> pEDBeans = JSON.parseArray(jsonStr2, provinceED.class);
            for (provinceED pEDbean : pEDBeans) {
                pEDbean.setProvinceShortName(pBean.getProvinceShortName());
                pEDbean.setLocationId(pBean.getLocationId());
                String pdId = pBean.getProvinceShortName()+ pEDbean.getDateId();
                pEDbean.setPdId(pdId);
                //saveUtils.saveEDData(pEDbean);

            }
            //pBean.setStatisticsData(dataStr);
            pBean.setCities(null);
            //saveUtils.saveData(pBean); 默认存储城市，存储省份数据还需改sql再使用


        }
        conn.close();
        System.out.println("存储完毕");
    }
}
