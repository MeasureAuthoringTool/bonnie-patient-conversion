package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmComponent {
    private String qdmVersion;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_type")
    private String type;

    private JsonNode result;
    private QdmCode code;
    private QdmPeriod referenceRange;
}
