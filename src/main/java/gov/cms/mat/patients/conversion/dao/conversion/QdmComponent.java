package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmComponent {
    String qdmVersion;

    @JsonProperty("_id")
    String id;

    @JsonProperty("_type")
    String type;

    JsonNode result;
    QdmCode code;
    QdmPeriod referenceRange;
}
