package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedData {

    private String type; // always null

    @JsonProperty("is_shared")
    private Boolean isShared;

    @JsonProperty("origin_data")
    private JsonNode originData;

    @JsonProperty("test_id")
    private String testId; // always null
    @JsonProperty("medical_record_number")
    private String medicalRecordNumber;
    @JsonProperty("medical_record_assigner")
    private String medicalRecordAssigner; // always in Bonnie never used
    private String description; // always null
    @JsonProperty("description_category")
    private String descriptionCategory; // always null

    @JsonProperty("insurance_providers")
    private String insuranceProviders; // ignored

    @JsonProperty("source_data_criteria")
    private JsonNode sourceDataCriteria; // ignored
}
