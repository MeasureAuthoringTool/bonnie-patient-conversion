package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ImmunizationAdministeredNegationConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private ImmunizationAdministeredNegationConverter immunizationAdministeredNegationConverter;

    @Test
    void getQdmType() {
        assertEquals(ImmunizationAdministeredNegationConverter.QDM_TYPE, immunizationAdministeredNegationConverter.getQdmType());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result);
    }

    @Test
    void convertToFhirNegationEmptyObjects() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setDosage(createDosage());
        qdmDataElement.setSupply(createSupply());
        qdmDataElement.setDaysSupplied(10);
        qdmDataElement.setFrequency(createFrequency());
        qdmDataElement.setRefills(5);
        qdmDataElement.setRoute(createRoute());

        qdmDataElement.setSetting(createSetting());

        qdmDataElement.setReason(createReason());
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setPrescriber(createPrescriber());


        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result);

        checkDataElementCodeableConcept(result.getFhirResource().getMedicationCodeableConcept());
        checkDosageType(result.getFhirResource().getDosageInstructionFirstRep().getDoseAndRateFirstRep().getDose());

        checkSupply(result.getFhirResource().getDispenseRequest().getQuantity());
        checkDaysSupplied(result.getFhirResource().getDispenseRequest().getExpectedSupplyDuration());
        checkFrequency(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getCode());
        checkRefills(result.getFhirResource().getDispenseRequest().getNumberOfRepeatsAllowed());
        checkRoute(result.getFhirResource().getDosageInstructionFirstRep().getRoute());

        assertEquals(2, result.getFhirResource().getReasonCode().size());
        checkReason(result.getFhirResource().getReasonCode().get(0));

        checkRelevantDateTime(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getEvent().get(0).getValue());
        checkRelevantPeriod(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getRepeat().getBoundsPeriod());
        checkAuthorDatetime(result.getFhirResource().getAuthoredOn());
        checkNegationRationaleTypeCodeableConcept(result.getFhirResource().getReasonCode().get(1));

    }

    private void checkRefills(int numberOfRepeatsAllowed) {
        assertEquals(5, numberOfRepeatsAllowed);
    }

    private void checkDaysSupplied(Duration duration) {
        assertEquals(10, duration.getValue().intValue());
        assertEquals("d", duration.getUnit());
        assertEquals(UCUM_SYSTEM, duration.getSystem());
    }

    private void checkDosageType(Type dose) {
        assertThat(dose, instanceOf(Quantity.class));
        Quantity quantity = (Quantity) dose;
        checkDosage(quantity);
    }

    @Test
    void convertToFhirEmptyObjects() {
        // convert only processes if negation rational is present
        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNull(result);
    }

    private void checkNegation(QdmToFhirConversionResult<MedicationRequest> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(MedicationRequest.MedicationRequestStatus.COMPLETED, result.getFhirResource().getStatus());
        assertEquals(0, result.getConversionMessages().size());
        assertTrue(result.getFhirResource().getDoNotPerform());
    }


}