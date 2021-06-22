package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MedicationDispensedConverter extends ConverterBase<MedicationDispense> {
    public static final String QDM_TYPE = "QDM::MedicationDispensed";

    public MedicationDispensedConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<MedicationDispense> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var medicationDispense = new MedicationDispense();
        medicationDispense.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            medicationDispense.setMedication(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        medicationDispense.setId(qdmDataElement.getId());

        if (qdmDataElement.getDosage() != null) {
            log.info("Dosage is found and not mapped");
            conversionMessages.add("Cannot convert QDM dosage (quantity) to a Fhir dosage instruction");
        }

        if (qdmDataElement.getSupply() != null) {
            medicationDispense.setQuantity(convertQuantity(qdmDataElement.getSupply()));
        }

        if (qdmDataElement.getDaysSupplied() != null) {
            var quantity = createQuantity(qdmDataElement.getDaysSupplied(), "d");
            medicationDispense.setDaysSupply(quantity);
        }

        if (qdmDataElement.getFrequency() != null) {
            if (qdmDataElement.getFrequency().getCode() == null) {
                conversionMessages.add("Frequency code is null");
            } else {
                var dosage = medicationDispense.getDosageInstructionFirstRep();
                var timing = dosage.getTiming();
                timing.setCode(convertToCodeableConcept(qdmDataElement.getFrequency()));
            }
        }

        if (qdmDataElement.getSetting() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "setting");
        }

        processTimes(qdmDataElement, medicationDispense);

        if (qdmDataElement.getPrescriber() != null) {
            medicationDispense.addAuthorizingPrescription(createPractitionerReference(qdmDataElement.getPrescriber()));
        }

        if (qdmDataElement.getDispenser() != null) {
            medicationDispense.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getDispenser()));
        }

        if (qdmDataElement.getRoute() != null) {
            medicationDispense.getDosageInstructionFirstRep().setRoute(convertToCodeableConcept(qdmDataElement.getRoute()));
        }


        if (!processNegation(qdmDataElement, medicationDispense)) {
            medicationDispense.setStatus(MedicationDispense.MedicationDispenseStatus.COMPLETED);
        }

        return QdmToFhirConversionResult.<MedicationDispense>builder()
                .fhirResource(medicationDispense)
                .conversionMessages(conversionMessages)
                .build();
    }

    private void processTimes(QdmDataElement qdmDataElement, MedicationDispense medicationDispense) {
        if (qdmDataElement.getRelevantDatetime() != null) {
            medicationDispense.setWhenHandedOver(qdmDataElement.getRelevantDatetime());
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            var dosage = medicationDispense.getDosageInstructionFirstRep();
            var timing = dosage.getTiming();
            timing.getRepeat().setBounds(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            var recordedExtension = createRecordedExtension(qdmDataElement.getAuthorDatetime());
            medicationDispense.getExtension().add(recordedExtension);
        }
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, MedicationDispense medicationDispense) {
        medicationDispense.setStatus(MedicationDispense.MedicationDispenseStatus.DECLINED);

        var codeableConcept = convertToCodeableConcept(qdmDataElement.getNegationRationale());
        medicationDispense.setStatusReason(codeableConcept);
    }
}
