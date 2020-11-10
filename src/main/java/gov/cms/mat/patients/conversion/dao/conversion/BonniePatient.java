package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
public class BonniePatient {
    @JsonProperty("_id")
    String id;

    List<String> givenNames;
    String familyName;
    String bundleId;

    @JsonProperty("provider_ids")
    String[] providerIds;

    List<ExpectedValues> expectedValues;

    String notes;

    @JsonProperty("measure_ids")
    List<String> measureIds;

    @JsonProperty("user_id")
    String userId;

    QdmPatient qdmPatient;

    public String identifier() {
        return id;
    }
}
