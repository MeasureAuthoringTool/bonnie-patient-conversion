package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmCode {
    String code;
    String system;
    String display;
    String version;

    @JsonProperty("_type")
    String type;

   String  descriptor;
}
