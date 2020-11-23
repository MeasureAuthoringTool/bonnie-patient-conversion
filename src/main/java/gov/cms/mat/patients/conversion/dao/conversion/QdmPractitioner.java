package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmPractitioner {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("_type")
    private String qdmType;

    private String qdmVersion;
    private String type;

    private QdmCodeSystem role;
    private QdmCodeSystem specialty;
    private QdmCodeSystem qualification;

    private QdmIdentifier identifier;
    private String hqmfOid;
    private String qrdaOid;
}
