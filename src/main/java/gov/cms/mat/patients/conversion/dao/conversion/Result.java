package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {
    String code;
    String version;
    String system;
    String display;
    String unit;
    Integer value;
}
