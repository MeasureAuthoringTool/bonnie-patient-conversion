package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacilityLocation {
    private String qdmVersion;
    @JsonProperty("_type")
    private String type;
    @JsonProperty("_id")
    private String id;
    private QdmCodeSystem code;

    private QdmPeriod locationPeriod;
}
