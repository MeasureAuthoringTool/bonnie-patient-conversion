package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.MedicationRequestConverter;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MedicationOrderConverter extends ConverterBase<MedicationRequest> implements MedicationRequestConverter {
    public static final String QDM_TYPE = "QDM::MedicationOrder";

    public MedicationOrderConverter(CodeSystemEntriesService codeSystemEntriesService,
                                    FhirContext fhirContext,
                                    ObjectMapper objectMapper,
                                    ValidationService validationService) {
        super(codeSystemEntriesService, fhirContext, objectMapper, validationService);
    }

    @Override
    public String getQdmType() {
        return QDM_TYPE;
    }

    @Override
    public QdmToFhirConversionResult<MedicationRequest> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8175-medication-order
        // status	Constrain to active, completed, on-hold
        return convertToFhirMedicationRequest(fhirPatient,
                qdmDataElement,
                this,
                MedicationRequest.MedicationRequestIntent.ORDER,
                true);
    }
}

