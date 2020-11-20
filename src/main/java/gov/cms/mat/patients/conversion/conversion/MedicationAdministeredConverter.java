package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MedicationAdministeredConverter extends ConverterBase<MedicationAdministration> {
    public static final String QDM_TYPE = "QDM::MedicationAdministered";

    public MedicationAdministeredConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<MedicationAdministration> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        MedicationAdministration medicationAdministration = new MedicationAdministration();
        medicationAdministration.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            medicationAdministration.setMedication(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        medicationAdministration.setId(qdmDataElement.getId());

        if (qdmDataElement.getDosage() != null) {
            medicationAdministration.getDosage().setDose(convertQuantity(qdmDataElement.getDosage()));
        }

        if (qdmDataElement.getRoute() != null) {
            medicationAdministration.getDosage().setRoute(convertToCodeableConcept(qdmDataElement.getRoute()));
        }

        // This object if not null then all the elements in the object is null
        if (qdmDataElement.getFrequency() != null) {

            if (StringUtils.isBlank(qdmDataElement.getFrequency().getCodeSystem())) {
                log.info("qdmDataElement.getFrequency() all elements are null");
            } else {
                log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "frequency");
            }
        }

        if (qdmDataElement.getReason() != null) {
            medicationAdministration.getReasonCode().add(convertToCodeableConcept(qdmDataElement.getReason()));
        }

        boolean havePeriod = false;

        if (qdmDataElement.getRelevantPeriod() != null) {
            medicationAdministration.setEffective(convertPeriod(qdmDataElement.getRelevantPeriod()));
            havePeriod = true;
        }

        if (!havePeriod && qdmDataElement.getRelevantDatetime() != null) {
            medicationAdministration.setEffective(new DateTimeType(qdmDataElement.getRelevantDatetime()));
        }

        if (qdmDataElement.getPerformer() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "performer");
            medicationAdministration.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getPerformer()));
        }

        if (!processNegation(qdmDataElement, medicationAdministration)) {
            medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.UNKNOWN);
            conversionMessages.add(NO_STATUS_MAPPING);
        }

        return QdmToFhirConversionResult.<MedicationAdministration>builder()
                .fhirResource(medicationAdministration)
                .conversionMessages(conversionMessages)
                .build();
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, MedicationAdministration medicationAdministration) {
        medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.NOTDONE);

        medicationAdministration.getStatusReason().add(convertToCodeableConcept(qdmDataElement.getNegationRationale()));

        if (qdmDataElement.getAuthorDatetime() != null) {
            medicationAdministration.getExtension().add(createRecordedExtension(qdmDataElement.getAuthorDatetime()));
        }
    }
}
