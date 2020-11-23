package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmComponentResult {
    private String code;
    private String version;
    private String descriptor;
    private String system;

    private String unit;
    private String value;
}
