package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Timing;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UNEXPECTED_DATA_LOG_MESSAGE;

public interface MedicationRequestConverter extends FhirCreator, DataElementFinder {

    default QdmToFhirConversionResult<MedicationRequest> convertToFhirMedicationRequest(Patient fhirPatient,
                                                                                        QdmDataElement qdmDataElement,
                                                                                        ConverterBase<MedicationRequest> converterBase,
                                                                                        MedicationRequest.MedicationRequestIntent intent,
                                                                                        boolean setStatusToUnknown) {
        List<String> conversionMessages = new ArrayList<>();

        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.setSubject(createPatientReference(fhirPatient));

        medicationRequest.setIntent(intent);

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            medicationRequest.setMedication(getMedicationCodeableConcept(qdmDataElement.getDataElementCodes(), converterBase));
        }

        medicationRequest.setId(qdmDataElement.getId());

        if (qdmDataElement.getDosage() != null) {
            createDosage(qdmDataElement, medicationRequest);
        }

        if (qdmDataElement.getSupply() != null) {
            medicationRequest.getDispenseRequest().setQuantity(convertQuantity(qdmDataElement.getSupply()));
        }

        if (qdmDataElement.getDaysSupplied() != null) {
            createDuration(qdmDataElement, medicationRequest);
        }

        if (qdmDataElement.getFrequency() != null) {
            createFrequency(qdmDataElement, converterBase, conversionMessages, medicationRequest);
        }

        if (qdmDataElement.getRefills() != null) {
            MedicationRequest.MedicationRequestDispenseRequestComponent dispenseRequest = medicationRequest.getDispenseRequest();
            dispenseRequest.setNumberOfRepeatsAllowed(qdmDataElement.getRefills());
        }

        if (qdmDataElement.getRoute() != null) {
            Dosage dosage = medicationRequest.getDosageInstructionFirstRep();
            dosage.setRoute(converterBase.convertToCodeableConcept(qdmDataElement.getRoute()));
        }

        // if we had data would be hard to convert code to enum
        if (qdmDataElement.getSetting() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "setting");
        }

        if (qdmDataElement.getReason() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "reason");
            medicationRequest.getReasonCode()
                    .add(converterBase.convertToCodeableConcept(qdmDataElement.getReason()));
        }


        if (qdmDataElement.getRelevantDatetime() != null) {
            Dosage dosage = medicationRequest.getDosageInstructionFirstRep();
            dosage.getTiming().setEvent(List.of(new DateTimeType(qdmDataElement.getRelevantDatetime())));
        }


        if (qdmDataElement.getRelevantPeriod() != null) {
            Dosage dosage = medicationRequest.getDosageInstructionFirstRep();
            Timing timing = dosage.getTiming();
            Timing.TimingRepeatComponent timingRepeatComponent = timing.getRepeat();
            timingRepeatComponent.setBounds(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        medicationRequest.setAuthoredOn(qdmDataElement.getAuthorDatetime());


        if (!converterBase.processNegation(qdmDataElement, medicationRequest) && setStatusToUnknown) {
            // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8173-medication-discharge
            // 	Constrain to active, completed, on-hold
            medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.UNKNOWN);
            conversionMessages.add(NO_STATUS_MAPPING);
        }


        if (qdmDataElement.getPrescriber() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "prescriber");
            medicationRequest.setRequester(createPractitionerReference(qdmDataElement.getPrescriber()));
        }

        return QdmToFhirConversionResult.<MedicationRequest>builder()
                .fhirResource(medicationRequest)
                .conversionMessages(conversionMessages)
                .build();
    }

    private void createFrequency(QdmDataElement qdmDataElement,
                                 ConverterBase<MedicationRequest> converterBase,
                                 List<String> conversionMessages,
                                 MedicationRequest medicationRequest) {
        if (qdmDataElement.getFrequency().getCode() == null) {
            conversionMessages.add("Frequency code is null");
        } else {
            Dosage dosage = medicationRequest.getDosageInstructionFirstRep();
            Timing timing = dosage.getTiming();
            timing.setCode(converterBase.convertToCodeableConcept(qdmDataElement.getFrequency()));
        }
    }

    private void createDosage(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        Quantity quantity = convertQuantity(qdmDataElement.getDosage());

        Dosage dosage = medicationRequest.getDosageInstructionFirstRep();
        dosage.getDoseAndRateFirstRep().setDose(quantity);
    }

    private void createDuration(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        MedicationRequest.MedicationRequestDispenseRequestComponent dispenseRequest = medicationRequest.getDispenseRequest();
        Duration duration = new Duration();
        duration.setUnit("d");
        duration.setSystem(UCUM_SYSTEM);
        duration.setValue(qdmDataElement.getDaysSupplied());
        dispenseRequest.setExpectedSupplyDuration(duration);
    }

    private CodeableConcept getMedicationCodeableConcept(List<QdmCodeSystem> dataElementCodes, ConverterBase<MedicationRequest> converterBase) {
      return new CodeableConcept().addCoding(converterBase.convertToCoding(dataElementCodes));
    }
}
