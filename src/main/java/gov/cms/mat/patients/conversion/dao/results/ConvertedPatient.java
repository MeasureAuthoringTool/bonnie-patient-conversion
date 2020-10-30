package gov.cms.mat.patients.conversion.dao.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConvertedPatient {
    @JsonProperty("fhir_patient")
    private final JsonNode fhirPatient;

    private final ConversionOutcome outcome;
}
