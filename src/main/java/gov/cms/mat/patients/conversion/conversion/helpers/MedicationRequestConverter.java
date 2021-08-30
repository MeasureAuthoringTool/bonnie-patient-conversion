package gov.cms.mat.patients.conversion.conversion.helpers;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UNEXPECTED_DATA_LOG_MESSAGE;

public interface MedicationRequestConverter extends FhirCreator, DataElementFinder {

    default QdmToFhirConversionResult<MedicationRequest> convertToFhirMedicationRequest(Patient fhirPatient,
                                                                                        QdmDataElement qdmDataElement,
                                                                                        ConverterBase<MedicationRequest> converterBase,
                                                                                        MedicationRequest.MedicationRequestIntent intent,
                                                                                        boolean setStatusToUnknown) {
        List<String> conversionMessages = new ArrayList<>();

        var medicationRequest = new MedicationRequest();
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
            var dosage = medicationRequest.getDosageInstructionFirstRep();
            dosage.setRoute(converterBase.convertToCodeableConcept(qdmDataElement.getRoute()));
        }

        if (qdmDataElement.getSetting() != null) {
            medicationRequest.getCategory()
                    .add(converterBase.convertToCodeableConcept(qdmDataElement.getSetting()));
        }

        if (qdmDataElement.getReason() != null) {
            medicationRequest.getReasonCode()
                    .add(converterBase.convertToCodeableConcept(qdmDataElement.getReason()));
        }

        if (qdmDataElement.getRelevantDatetime() != null) {
            var dosage = medicationRequest.getDosageInstructionFirstRep();
            qdmDataElement.getRelevantDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            List<DateTimeType> relevantDateTime = new ArrayList<>() {{
                add(qdmDataElement.getRelevantDatetime());
            }};
            dosage.getTiming().setEvent(relevantDateTime);
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            var dosage = medicationRequest.getDosageInstructionFirstRep();
            var timing = dosage.getTiming();
            var timingRepeatComponent = timing.getRepeat();
            timingRepeatComponent.setBounds(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (qdmDataElement.getActiveDatetime() != null) {
            var dosage = medicationRequest.getDosageInstructionFirstRep();
            qdmDataElement.getActiveDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            var period = new Period()
                    .setStartElement(qdmDataElement.getActiveDatetime());
            dosage.getTiming().getRepeat().setBounds(period);
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            qdmDataElement.getAuthorDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            medicationRequest.setAuthoredOnElement(qdmDataElement.getAuthorDatetime());
        }

        if (!converterBase.processNegation(qdmDataElement, medicationRequest) && setStatusToUnknown) {
            medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.COMPLETED);
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
            var dosage = medicationRequest.getDosageInstructionFirstRep();
            var timing = dosage.getTiming();
            timing.setCode(converterBase.convertToCodeableConcept(qdmDataElement.getFrequency()));
        }
    }

    private void createDosage(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        var quantity = convertQuantity(qdmDataElement.getDosage());

        var dosage = medicationRequest.getDosageInstructionFirstRep();
        dosage.getDoseAndRateFirstRep().setDose(quantity);
    }

    private void createDuration(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        var dispenseRequest = medicationRequest.getDispenseRequest();
        var duration = new Duration();
        duration.setUnit("d");
        duration.setSystem(UCUM_SYSTEM);
        duration.setValue(qdmDataElement.getDaysSupplied());
        dispenseRequest.setExpectedSupplyDuration(duration);
    }

    private CodeableConcept getMedicationCodeableConcept(List<QdmCodeSystem> dataElementCodes, ConverterBase<MedicationRequest> converterBase) {
        return new CodeableConcept().addCoding(converterBase.convertToCoding(dataElementCodes));
    }
}
