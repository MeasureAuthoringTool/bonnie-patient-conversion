package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {
    @JsonProperty("_id")
    private String id;

    private String qdmVersion;

    @JsonProperty("_type")
    private String qdmType;

    private String hqmfOid;
    private String qrdaOid;
    private QdmCodeSystem relationship;
    private QdmIdentifier identifier;
    private String type;
}
