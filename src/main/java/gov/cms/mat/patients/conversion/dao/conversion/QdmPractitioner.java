package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmPractitioner {
    @JsonProperty("_id")
    String id;

    @JsonProperty("_type")
    String qdmType;

    String qdmVersion;
    String type;

    QdmCodeSystem role;
    QdmCodeSystem specialty;
    QdmCodeSystem qualification;

    QdmIdentifier identifier;
    String hqmfOid;
    String qrdaOid;
}
