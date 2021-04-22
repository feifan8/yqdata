package getData.saveData;

import getData.bean.CovidBean;
import getData.bean.provinceED;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//使用时填写数据库账号
public abstract class saveUtils {
    public static String saveData(CovidBean bean) throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root");
        //String sql = new String("replace into province (provinceName,provinceShortName,cityName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,locationId,pid,cities,dateTime) value(?,?,?,?,?,?,?,?,?,?,?,?)");
        String sql = new String("replace into city (provinceName,provinceShortName,cityName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,locationId,pid,cities,dateTime) value(?,?,?,?,?,?,?,?,?,?,?,?)");

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, bean.getProvinceName());
        ps.setString(2, bean.getProvinceName());
        ps.setString(3, bean.getCityName());
        ps.setLong(4, bean.getCurrentConfirmedCount());
        ps.setLong(5, bean.getConfirmedCount());
        ps.setLong(6, bean.getSuspectedCount());
        ps.setLong(7, bean.getCuredCount());
        ps.setLong(8, bean.getDeadCount());
        ps.setString(9, bean.getLocationId());
        ps.setString(10, bean.getPid());
        //ps.setString(11, bean.getStatisticsData());
        ps.setString(11, bean.getCities());
        ps.setString(12, bean.getDateTime());
        ps.executeUpdate();
        ps.close();
        conn.close();
        String str = "存储到数据库";
        System.out.println(str);
        return str;
    }

    public static String saveEDData(provinceED bean) throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root");
        //String sql = new String("replace into province (provinceName,provinceShortName,cityName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,locationId,pid,cities,dateTime) value(?,?,?,?,?,?,?,?,?,?,?,?)");
        String sql = new String("replace into provinceed (dateId,provinceShortName,locationId,confirmedCount,currentConfirmedCount,currentConfirmedIncr,confirmedIncr,curedCount,curedIncr,suspectedCountIncr,suspectedCount,deadCount,deadIncr,pdId) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, bean.getDateId());
        ps.setString(2, bean.getProvinceShortName());
        ps.setString(3, bean.getLocationId());
        ps.setInt(4, bean.getConfirmedCount());
        ps.setInt(5, bean.getCurrentConfirmedCount());
        ps.setInt(6, bean.getCurrentConfirmedIncr());
        ps.setInt(7, bean.getConfirmedIncr());
        ps.setInt(8, bean.getCuredCount());
        ps.setInt(9, bean.getCuredIncr());
        ps.setInt(10, bean.getSuspectedCountIncr());
        ps.setInt(11, bean.getSuspectedCount());
        ps.setInt(12, bean.getDeadCount());
        ps.setInt(13, bean.getDeadIncr());
        ps.setString(14, bean.getPdId());
        ps.executeUpdate();
        ps.close();
        conn.close();
        String str = "存储到数据库";
        System.out.println(str);
        return str;
    }
}
