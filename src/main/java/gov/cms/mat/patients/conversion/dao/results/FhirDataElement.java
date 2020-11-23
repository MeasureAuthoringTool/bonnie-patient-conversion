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
    private String codeListId;
    private String valueSetTitle;
    private String description;

    @JsonProperty("fhir_resource")
    private JsonNode fhirResource;

    private ConversionOutcome outcome;

    @JsonIgnore
    private String fhirType;
    @JsonIgnore
    private String fhirId;

    @JsonIgnore
    private IBaseResource fhirObject;
}
