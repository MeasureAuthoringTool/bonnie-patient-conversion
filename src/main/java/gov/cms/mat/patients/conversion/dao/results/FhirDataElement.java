package gov.cms.mat.patients.conversion.dao.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import org.hl7.fhir.instance.model.api.IBaseResource;

@Data
@Builder
public class FhirDataElement {

    String codeListId;
    String valueSetTitle;
    String description;

    @JsonProperty("fhir_resource")
    JsonNode fhirResource;

    ConversionOutcome outcome;

    @JsonIgnore
    String fhirType;
    @JsonIgnore
    String fhirId;

    @JsonIgnore
    IBaseResource fhirObject;
}
