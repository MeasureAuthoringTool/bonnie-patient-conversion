package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Participant {
    @JsonProperty("_id")
    String id;

    String qdmVersion;

    @JsonProperty("_type")
    String qdmType;

    String hqmfOid;
    String qrdaOid;
    QdmCodeSystem relationship;
    QdmIdentifier identifier;
    String type;
}
