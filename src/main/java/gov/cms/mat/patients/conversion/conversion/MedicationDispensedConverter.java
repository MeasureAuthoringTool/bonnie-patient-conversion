package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Timing;
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

        MedicationDispense medicationDispense = new MedicationDispense();
        medicationDispense.setSubject(createPatientReference(fhirPatient));

        medicationDispense.setMedication(convertToCodeSystems(codeSystemEntriesService, qdmDataElement.getDataElementCodes()));
        medicationDispense.setId(qdmDataElement.getId());

        if (qdmDataElement.getDosage() != null) {
            //Todo Dosage not mapped
            log.debug("Dosage is found");
//            medicationDispense.setDosageInstruction();
        }


        if (qdmDataElement.getSupply() != null) {
            medicationDispense.setQuantity(convertQuantity(qdmDataElement.getSupply()));
        }

        if (qdmDataElement.getDaysSupplied() != null) {
            Quantity quantity = createQuantity(qdmDataElement.getDaysSupplied(), "d");
            medicationDispense.setDaysSupply(quantity);
        }

        if (qdmDataElement.getFrequency() != null) {
            if (qdmDataElement.getFrequency().getCode() == null) {
                conversionMessages.add("Frequency code is null");
            } else {
                Dosage dosage = medicationDispense.getDosageInstructionFirstRep();
                Timing timing = dosage.getTiming();
                timing.setCode(convertToCodeableConcept(codeSystemEntriesService, qdmDataElement.getFrequency()));
            }
        }

        //No Refills mapped
        //Route not mapped

        if (qdmDataElement.getSetting() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "setting");
        }

        if (qdmDataElement.getRelevantDatetime() != null) {
            medicationDispense.setWhenHandedOver(qdmDataElement.getRelevantDatetime());
        }

        //Relevent Period

        if (qdmDataElement.getPrescriber() != null) {
            medicationDispense.addAuthorizingPrescription(createPractitionerReference(qdmDataElement.getPrescriber()));
        }

        if (qdmDataElement.getDispenser() != null) {
            medicationDispense.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getDispenser()));
        }

        if (!processNegation(qdmDataElement, medicationDispense)) {
            medicationDispense.setStatus(MedicationDispense.MedicationDispenseStatus.UNKNOWN);
            conversionMessages.add(NO_STATUS_MAPPING);
        }

        return QdmToFhirConversionResult.<MedicationDispense>builder()
                .fhirResource(medicationDispense)
                .conversionMessages(conversionMessages)
                .build();
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, MedicationDispense medicationDispense) {
        medicationDispense.setStatus(MedicationDispense.MedicationDispenseStatus.DECLINED);

        CodeableConcept codeableConcept = convertToCodeableConcept(codeSystemEntriesService, qdmDataElement.getNegationRationale());
        medicationDispense.setStatusReason(codeableConcept);

        if (qdmDataElement.getAuthorDatetime() != null) {
            Extension extension = new Extension(QICORE_RECORDED);
            extension.setValue(new DateTimeType(qdmDataElement.getAuthorDatetime()));
            medicationDispense.setExtension(List.of(extension));
        }
    }
}
