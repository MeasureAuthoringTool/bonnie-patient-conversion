package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ImmunizationAdministeredConverter extends ConverterBase<Immunization> {
    public static final String QDM_TYPE = "QDM::ImmunizationAdministered";

    public ImmunizationAdministeredConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Immunization> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        if (qdmDataElement.getNegationRationale() != null) {
            log.debug("Not creating Immunization due to negation");
            return null;
        } else {
            return convertToFhirNoNegation(fhirPatient, qdmDataElement);
        }
    }

    private QdmToFhirConversionResult<Immunization> convertToFhirNoNegation(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var immunization = new Immunization();
        immunization.setPatient(createPatientReference(fhirPatient));

        immunization.setStatus(Immunization.ImmunizationStatus.COMPLETED);

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            immunization.setVaccineCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        immunization.setId(qdmDataElement.getId());

        if (qdmDataElement.getDosage() != null) {
            immunization.setDoseQuantity(convertQuantity(qdmDataElement.getDosage()));
        }

        if (qdmDataElement.getRoute() != null) {
            immunization.setRoute(convertToCodeableConcept(qdmDataElement.getRoute()));
        }

        if (qdmDataElement.getReason() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "reason");
            immunization.getReasonCode()
                    .add(convertToCodeableConcept(qdmDataElement.getReason()));
        }

        if (qdmDataElement.getRelevantDatetime() != null) {
            immunization.setOccurrence(qdmDataElement.getRelevantDatetime());
        }

        immunization.setRecordedElement(qdmDataElement.getAuthorDatetime());

        if (qdmDataElement.getPerformer() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "performer");
            immunization.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getPerformer()));
        }

        return QdmToFhirConversionResult.<Immunization>builder()
                .fhirResource(immunization)
                .conversionMessages(conversionMessages)
                .build();
    }
}
