package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacilityLocation {

    String qdmVersion;
    @JsonProperty("_type")
    String type;
    @JsonProperty("_id")
    String id;
    QdmCodeSystem code;

    QdmPeriod locationPeriod;
}
