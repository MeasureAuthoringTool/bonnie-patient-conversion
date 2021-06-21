package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.Quantity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class MedicationDispensedConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private MedicationDispensedConverter medicationDispensedConverter;

    @Test
    void getQdmType() {
        assertEquals(MedicationDispensedConverter.QDM_TYPE, medicationDispensedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setDosage(createDosage()); // not mapped
        qdmDataElement.setSupply(createSupply());
        qdmDataElement.setDaysSupplied(22);
        qdmDataElement.setFrequency(createFrequency());
        qdmDataElement.setSetting(createSetting()); // not mapped
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setPrescriber(createPrescriber());
        qdmDataElement.setDispenser(createDispenser());
        qdmDataElement.setRoute(createRoute());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        QdmToFhirConversionResult<MedicationDispense> result =
                medicationDispensedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        checkDataElementCodeableConcept(result.getFhirResource().getMedicationCodeableConcept());
        checkSupply(result.getFhirResource().getQuantity());
        checkDaysSupply(result.getFhirResource().getDaysSupply());
        checkFrequency(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getCode());
        checkRelevantDateTime(result.getFhirResource().getWhenHandedOver());
        checkRelevantPeriod(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getRepeat().getBoundsPeriod());
        checkPrescriber(result.getFhirResource().getAuthorizingPrescriptionFirstRep());
        checkDispenser(result.getFhirResource().getPerformerFirstRep().getActor());

        checkRoute(result.getFhirResource().getDosageInstructionFirstRep().getRoute());

        assertEquals(MedicationDispense.MedicationDispenseStatus.COMPLETED, result.getFhirResource().getStatus());

        assertEquals(1, result.getFhirResource().getExtension().size());
        checkRecordedExtension(result.getFhirResource().getExtension().get(0));

        assertEquals(1, result.getConversionMessages().size());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        QdmToFhirConversionResult<MedicationDispense> result =
                medicationDispensedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkNegationRationaleTypeCodeableConcept(result.getFhirResource().getStatusReasonCodeableConcept());

        assertEquals(1, result.getFhirResource().getExtension().size());
        checkRecordedExtension(result.getFhirResource().getExtension().get(0));

        assertEquals(MedicationDispense.MedicationDispenseStatus.DECLINED, result.getFhirResource().getStatus());
        assertEquals(0, result.getConversionMessages().size());
    }

    @Test
    void convertToFhirBadFrequency() {
        qdmDataElement.setFrequency(new QdmCodeSystem());

        QdmToFhirConversionResult<MedicationDispense> result =
                medicationDispensedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals("Frequency code is null", result.getConversionMessages().get(0));
    }

    private void checkDaysSupply(Quantity daysSupply) {
        assertEquals(22, daysSupply.getValue().intValue());
        assertEquals("d", daysSupply.getCode());
    }


    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<MedicationDispense> result =
                medicationDispensedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(MedicationDispense.MedicationDispenseStatus.COMPLETED, result.getFhirResource().getStatus());

        assertTrue(result.getConversionMessages().isEmpty());
    }
}