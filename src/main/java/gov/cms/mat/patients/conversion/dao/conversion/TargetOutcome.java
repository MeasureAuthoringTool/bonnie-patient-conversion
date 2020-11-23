package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TargetOutcome {
    private String code;
    private String system;
    private String display;
    private String version;
    private String unit;
    private Integer value;
}
