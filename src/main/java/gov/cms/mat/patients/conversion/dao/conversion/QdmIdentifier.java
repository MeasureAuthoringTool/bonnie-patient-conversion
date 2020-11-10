package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmIdentifier {
    @JsonProperty("_id")
    String id;

    @JsonProperty("_type")
    String type;

    String qdmVersion;
    String namingSystem;
    String value;
}
