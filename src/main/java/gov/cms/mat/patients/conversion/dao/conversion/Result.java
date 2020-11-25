package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {
    private String code;
    private String version;
    private String system;
    private String display;
    private String unit;
    private Integer value;
}
