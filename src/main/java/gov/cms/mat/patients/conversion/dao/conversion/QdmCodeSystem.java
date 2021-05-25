package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QdmCodeSystem {
    private String code;
    private String system;
    private String display;
    private String version;
    @JsonProperty("_type")
    private String type;

    private String codeSystem;
}
