package getData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class countryBean {
    private String id;
    private String continents;
    private String provinceName;
    private Integer confirmedCount;
    private Integer curedCount;
    private Integer deadCount;
    private Double deadRate;
    private String countryFullName;
}
