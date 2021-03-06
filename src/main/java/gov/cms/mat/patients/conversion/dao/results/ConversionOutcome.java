package gov.cms.mat.patients.conversion.dao.results;

import ca.uhn.fhir.validation.SingleValidationMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ConversionOutcome {
    List<String> conversionMessages;
    List<SingleValidationMessage> validationMessages;
}
