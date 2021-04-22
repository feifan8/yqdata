package getData.crawler;

import getData.bean.CovidBean;
import getData.util.HttpUtils;
import getData.util.TimeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = DatasourseApplication.class)
@Component
public class Covid19DataCrawler {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //@Scheduled(cron = "0 0 8 * * ?")
    //@Scheduled(cron = "0 0/20 * * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void testCrawling() throws Exception{
        String datetime = TimeUtils.format(System.currentTimeMillis(),"yyyy-MM-dd");
        //获取丁香园数据
        String html = HttpUtils.getHtml("https://ncov.dxy.cn/ncovh5/view/pneumonia");

        Document doc = (Document) Jsoup.parse(html);
        //取地区数据
        String text = doc.select("script[id=getAreaStat]").toString();

        //正则表达式取json格式数据
        String pattern = "\\[(.*)\\]";//表达式
        Pattern reg = Pattern.compile(pattern);//编译正则对象
        Matcher matcher = reg.matcher(text);//与text匹配
        String jsonStr = "";
        if (matcher.find()){
            jsonStr = matcher.group(0);
            //System.out.println(jsonStr);
        }else {
            System.out.println("no match");
        }

        //解析json
        List<CovidBean> pCovidBeans = JSON.parseArray(jsonStr,CovidBean.class);
        for (CovidBean pBean : pCovidBeans){
            pBean.setDateTime(datetime);
            String citysStr = pBean.getCities();
            //System.out.println(pBean);
            List<CovidBean> covidBeans = JSON.parseArray(citysStr,CovidBean.class);
            for(CovidBean bean : covidBeans){
                bean.setDateTime(datetime);
                bean.setPid(pBean.getLocationId());
                bean.setProvinceShortName(pBean.getProvinceShortName());
                //System.out.println(bean);
                //将javabean转成jsonstr发给kafka
                String beanStr = JSON.toJSONString(bean);
                kafkaTemplate.send("covid19",bean.getPid(),beanStr);
            }
            String statisticDataUrl = pBean.getStatisticsData();
            String statisticData = HttpUtils.getHtml(statisticDataUrl);
            JSONObject jsonObject = JSON.parseObject(statisticData);
            String dataStr = jsonObject.getString("data");
            pBean.setStatisticsData(dataStr);
            pBean.setCities(null);
            String pBeanStr = JSON.toJSONString(pBean);
            kafkaTemplate.send("covid19",pBean.getLocationId(),pBeanStr);
        }
    }
}
