package gov.cms.mat.patients.conversion.dao.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import gov.cms.mat.patients.conversion.dao.conversion.ExpectedValues;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
public class ConversionResult {
    private final String id;

    @JsonProperty("patient_outcome")
    private final ConversionOutcome patientOutcome;

    @JsonProperty("fhir_patient")
    private final JsonNode fhirPatient;

    @JsonProperty("expected_values")
    List<ExpectedValues> expectedValues;

    @JsonProperty("measure_ids")
    List<String> measureIds;

    @JsonProperty("data_elements")
    private final List<FhirDataElement> dataElements;

    @JsonProperty("created_at")
    private final Instant createdAt;

    @JsonProperty("updated_at")
    private final Instant updatedAt;
}
