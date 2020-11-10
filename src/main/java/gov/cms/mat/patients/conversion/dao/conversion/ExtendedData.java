package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedData {
    String type; // always null

    @JsonProperty("is_shared")
    Boolean isShared;

    @JsonProperty("origin_data")
    JsonNode originData;

    @JsonProperty("test_id")
    String testId; // always null
    @JsonProperty("medical_record_number")
    String medicalRecordNumber;
    @JsonProperty("medical_record_assigner")
    String medicalRecordAssigner; // always in Bonnie never used
    String description; // always null
    @JsonProperty("description_category")
    String descriptionCategory; // always null

    @JsonProperty("insurance_providers")
    String insuranceProviders; // ignored

    @JsonProperty("source_data_criteria")
    JsonNode sourceDataCriteria; // ignored
}
