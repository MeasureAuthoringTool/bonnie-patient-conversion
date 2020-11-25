package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmCode {
    private String code;
    private String system;
    private String display;
    private String version;

    @JsonProperty("_type")
    private String type;

    private String descriptor;
}
