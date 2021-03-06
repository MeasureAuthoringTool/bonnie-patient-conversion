package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Diagnoses {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_type")
    private String type;
    private QdmCodeSystem code;
    private QdmCodeSystem presentOnAdmissionIndicator;
    private Integer rank;
    private String qdmVersion;
}
