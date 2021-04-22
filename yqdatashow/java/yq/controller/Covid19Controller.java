package yq.controller;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yq.bean.Result;
import yq.mapper.CovidMapper;

import java.util.List;
import java.util.Map;

@RestController //@controller @responsebody,返回格式json
@RequestMapping("covid")
public class Covid19Controller {

    @Autowired
    private CovidMapper covidMapper;

    //现存确诊较昨日变化
    @RequestMapping("getCurrentConfirmedIncr")
    public Result getCurrentConfirmedIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        //Map<String,Object> data = covidMapper.getCurrentConfirmedIncr(dateId);
        Integer data = covidMapper.getCurrentConfirmedIncr(dateId);
        String newdata="";
        if(data>0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //现存确诊
    @RequestMapping("getCurrentConfirmedCount")
    public Result getCurrentConfirmedCount(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateTime = "20210331";
        Long data = covidMapper.getCurrentConfirmedCount(dateTime);
        Result result = Result.success(data);
        return result;
    }

    //境外输入较昨日增长
    @RequestMapping("getFromOtherIncr")
    public Result getFromOtherIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateTime = "20210402";
        Long data = covidMapper.getFromOtherIncr(dateTime);
        String newdata="";
        if(data>0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //境外输入
    @RequestMapping("getFromOther")
    public Result getFromOther(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateTime = "20210402";
        Long data = covidMapper.getFromOther(dateTime);
        Result result = Result.success(data);
        return result;
    }

    //无症状较昨日
    @RequestMapping("getSuspectedIncr")
    public Result getSuspectedIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Integer data = covidMapper.getSuspectedIncr(dateId);
        String newdata="";
        if(data>=0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //无症状
    @RequestMapping("getSuspectedCount")
    public Result getSuspectedCount(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateTime = "20210331";
        Long data = covidMapper.getSuspectedCount(dateTime);
        Result result = Result.success(data);
        return result;
    }

    //累计确诊较昨日
    @RequestMapping("getConfirmedCountIncr")
    public Result getConfirmedCountIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Long data = covidMapper.getConfirmedCountIncr(dateId);
        String newdata="";
        if(data>=0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //累计确诊
    @RequestMapping("getConfirmedCount")
    public Result getConfirmedCount(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateTime = "20210331";
        Long data = covidMapper.getConfirmedCount(dateTime);
        Result result = Result.success(data);
        return result;
    }

    //累计确诊较昨日
    @RequestMapping("getCuredIncr")
    public Result getCuredIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Integer data = covidMapper.getCuredIncr(dateId);
        String newdata="";
        if(data>=0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //累计治愈
    @RequestMapping("getCuredCount")
    public Result getCuredCount(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Integer data = covidMapper.getCuredCount(dateId);
        Result result = Result.success(data);
        return result;
    }

    //累计确诊较昨日
    @RequestMapping("getDeadIncr")
    public Result getDeadIncr(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Integer data = covidMapper.getDeadIncr(dateId);
        String newdata="";
        if(data>=0){
            newdata = "+"+data.toString();
        }
        else {
            newdata = "-"+data.toString();
        }
        Result result = Result.success(newdata);
        return result;
    }

    //累计死亡
    @RequestMapping("getDeadCount")
    public Result getDeadCount(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String dateId = "20210330";
        Integer data = covidMapper.getDeadCount(dateId);
        Result result = Result.success(data);
        return result;
    }

    //全国地图数据
    @RequestMapping("getNationalMapData")
    public Result getNationalMapData(){
        //String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        String datetime = "2021-03-29";
        List<Map<String,Object>> data = covidMapper.getNationalMapData(datetime);
        return Result.success(data);
    }

    //省份数据
    @RequestMapping("getProvinceData")
    public Result getProvinceData(){
        List<Map<String,Object>> data = covidMapper.getProvinceData();
        return Result.success(data);
    }

    //境外输入饼图
    @RequestMapping("getImportData")
    public Result getImportData(){
        List<Map<String,Object>> data = covidMapper.getImportData();
        return Result.success(data);
    }

    //每天折线图
    @RequestMapping("getTimeData")
    public Result getTimeData(){
        List<Map<String,Object>> data = covidMapper.getTimeData();
        return Result.success(data);
    }

    //海外数据汇总
    @RequestMapping("getAllCountryData")
    public Result getAllCountryData(){
        List<Map<String,Object>> data = covidMapper.getAllCountryData();
        return Result.success(data);
    }

    //海外数据
    @RequestMapping("getCountryData")
    public Result getCountryData(){
        List<Map<String,Object>> data = covidMapper.getCountryData();
        return Result.success(data);
    }

    //全球地图
    @RequestMapping("getWorldMapData")
    public Result getWorldMapData(){
        List<Map<String,Object>> data = covidMapper.getWorldMapData();
        return Result.success(data);
    }

    //省份地图
    @RequestMapping("getProvinceMapData")
    public Result getProvinceMapData(String pname){
        String name =pname;
        if(("北京重庆天津上海").contains(name)){
            name=name+"市";
        }
        if(("宁夏").contains(name)){
            name=name+"回族自治区";
        }
        if(("内蒙古西藏").contains(name)){
            name=name+"自治区";
        }
        if(("广西").contains(name)){
            name=name+"壮族自治区";
        }
        if(("新疆").contains(name)){
            name=name+"维吾尔自治区";
        }
        if(!("北京重庆天津上海宁夏内蒙古西藏广西新疆").contains(name)){
            name=name+"省";
        }
        List<Map<String,Object>> data = covidMapper.getProvinceMapData(name);
        return Result.success(data);
    }

    @RequestMapping("getAllCityData")
    public Result getAllCityData(String pname){
        String name =pname;
        if(("北京重庆天津上海").contains(name)){
            name=name+"市";
        }
        if(("宁夏").contains(name)){
            name=name+"回族自治区";
        }
        if(("内蒙古西藏").contains(name)){
            name=name+"自治区";
        }
        if(("广西").contains(name)){
            name=name+"壮族自治区";
        }
        if(("新疆").contains(name)){
            name=name+"维吾尔自治区";
        }
        if(!("北京重庆天津上海宁夏内蒙古西藏广西新疆").contains(name)){
            name=name+"省";
        }
        List<Map<String,Object>> data = covidMapper.getAllCityData(name);
        return Result.success(data);
    }
}
