package yq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface CovidMapper {
    Integer getCurrentConfirmedIncr(String dateId);

    Long getCurrentConfirmedCount(String dateTime);

    Long getFromOtherIncr(String dateTime);

    Long getFromOther(String dateTime);

    Integer getSuspectedIncr(String dateId);

    Long getSuspectedCount(String dateTime);

    Long getConfirmedCountIncr(String dateId);

    Long getConfirmedCount(String dateTime);

    Integer getCuredIncr(String dateId);

    Integer getCuredCount(String dateId);

    Integer getDeadIncr(String dateId);

    Integer getDeadCount(String dateId);

    @Select("select `provinceShortName` as name, `confirmedCount` as value from covid19_2 where datetime = #{datetime}")
    List<Map<String, Object>> getNationalMapData(String dateTime);

    @Select("select `provinceShortName`,`currentConfirmedCount`,`confirmedCount`,`deadCount`,`curedCount` from province")
    List<Map<String,Object>> getProvinceData();

    List<Map<String,Object>> getImportData();

    List<Map<String,Object>> getTimeData();

    List<Map<String,Object>> getCountryData();

    List<Map<String,Object>> getAllCountryData();

    @Select("select `countryFullName` as name,`confirmedCount` as value from countrymap")
    List<Map<String,Object>> getWorldMapData();

    List<Map<String,Object>> getProvinceMapData(String name);

    List<Map<String,Object>> getAllCityData(String name);
}
