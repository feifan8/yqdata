package getData.bean;
/*
*
* 封装省份每一天的数据
* */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class provinceED {
    private String dateId;
    private String provinceShortName;
    private String locationId;
    private Integer confirmedCount;
    private Integer currentConfirmedCount;
    private Integer currentConfirmedIncr;
    private Integer confirmedIncr;
    private Integer curedCount;
    private Integer curedIncr;
    private Integer suspectedCountIncr;
    private Integer suspectedCount;
    private Integer deadCount;
    private Integer deadIncr;
    private String pdId;
}
