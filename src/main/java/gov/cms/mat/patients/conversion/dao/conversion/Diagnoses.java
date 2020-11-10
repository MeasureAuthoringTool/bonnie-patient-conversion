package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Diagnoses {
    @JsonProperty("_id")
    String id;
    @JsonProperty("_type")
    String type;
    QdmCodeSystem code;
    QdmCodeSystem presentOnAdmissionIndicator;
    Integer rank;
    String qdmVersion;
}
