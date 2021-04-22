package getData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CovidBean {
    private String provinceName;//省份
    private String provinceShortName;//省份短名
    private String cityName;
    private Integer currentConfirmedCount;//当前确诊人数
    private Integer confirmedCount;//累积确诊人数
    private Integer suspectedCount;//疑似病例人数
    private Integer curedCount;//治愈人数
    private Integer deadCount;//死亡人数
    private String locationId;//位置id
    private String pid;//城市id
    private String statisticsData;//每天统计数据
    private String cities;//下属城市
    private String dateTime;//日期
}