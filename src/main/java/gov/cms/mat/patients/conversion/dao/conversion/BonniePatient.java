package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BonniePatient {
    private List<String> givenNames;
    private String familyName;
    private String bundleId;
    @JsonProperty("provider_ids")
    private String[] providerIds;
    private List<ExpectedValues> expectedValues;
    private String notes;
    @JsonProperty("measure_ids")
    private List<String> measureIds;
    @JsonProperty("user_id")
    private String userId;
    private QdmPatient qdmPatient;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("group_id")
    private String groupId;
    
    public String identifier() {
        return id;
    }
}
