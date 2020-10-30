package gov.cms.mat.patients.conversion.conversion.results;

import gov.cms.mat.patients.conversion.dao.results.ConversionOutcome;
import lombok.Builder;
import lombok.Data;
import org.hl7.fhir.r4.model.Patient;

@Builder
@Data
public class QdmToFhirPatientResult {
    private final Patient fhirPatient;
    private final ConversionOutcome outcome;
}
