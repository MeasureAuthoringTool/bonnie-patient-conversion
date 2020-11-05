package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.MedicationRequestConverter;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ImmunizationAdministeredNegationConverter extends ConverterBase<MedicationRequest> implements MedicationRequestConverter {
    public static final String QDM_TYPE = "QDM::ImmunizationAdministered";

    public ImmunizationAdministeredNegationConverter(CodeSystemEntriesService codeSystemEntriesService,
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
        if (qdmDataElement.getNegationRationale() == null) {
            log.debug("Not creating Immunization due to element not having negation");
            return null;
        } else {
            return  convertToFhirNegation(fhirPatient, qdmDataElement);
        }
    }

    private QdmToFhirConversionResult<MedicationRequest> convertToFhirNegation(Patient fhirPatient, QdmDataElement qdmDataElement) {
        return convertToFhirMedicationRequest(fhirPatient,
                qdmDataElement,
                this,
                MedicationRequest.MedicationRequestIntent.ORDER,
                true);
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        convertNegationMedicationRequest(qdmDataElement, medicationRequest);
    }
}
