package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Type;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MedicationRequestTest extends FhirConversionTest {
    default void checkNegation(QdmToFhirConversionResult<MedicationRequest> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(MedicationRequest.MedicationRequestStatus.COMPLETED, result.getFhirResource().getStatus());
        assertEquals(0, result.getConversionMessages().size());
        assertTrue(result.getFhirResource().getDoNotPerform());

        var list = result.getFhirResource().getReasonCode();
        checkNegationRationaleTypeCodeableConcept(list.get(list.size() - 1)); // last guy
    }

    default void createMedicationRequestElement(QdmDataElement qdmDataElement) {
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

        // qdmDataElement.setActiveDatetime(createActiveDatetime());
    }

    default void checkMedicationRequest(QdmToFhirConversionResult<MedicationRequest> result) {

        checkDataElementCodeableConcept(result.getFhirResource().getMedicationCodeableConcept());
        checkDosageType(result.getFhirResource().getDosageInstructionFirstRep().getDoseAndRateFirstRep().getDose());

        checkSupply(result.getFhirResource().getDispenseRequest().getQuantity());
        checkDaysSupplied(result.getFhirResource().getDispenseRequest().getExpectedSupplyDuration());
        checkFrequency(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getCode());
        checkRefills(result.getFhirResource().getDispenseRequest().getNumberOfRepeatsAllowed());
        checkRoute(result.getFhirResource().getDosageInstructionFirstRep().getRoute());

        checkReason(result.getFhirResource().getReasonCode().get(0));

        checkRelevantDateTime(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getEvent().get(0));
        checkRelevantPeriod(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getRepeat().getBoundsPeriod());
        checkAuthorDatetime(result.getFhirResource().getAuthoredOnElement());

        //  checkActiveDatetimePeriod(result.getFhirResource().getDosageInstructionFirstRep().getTiming().getRepeat().getBoundsPeriod());
    }

    default void checkActiveDatetimePeriod(Period boundsPeriod) {
        checkActiveDatetime(boundsPeriod.getStart());
    }

    default void checkRefills(int numberOfRepeatsAllowed) {
        assertEquals(5, numberOfRepeatsAllowed);
    }

    default void checkDaysSupplied(Duration duration) {
        assertEquals(10, duration.getValue().intValue());
        assertEquals("d", duration.getUnit());
        assertEquals(UCUM_SYSTEM, duration.getSystem());
    }

    default void checkDosageType(Type dose) {
        assertThat(dose, instanceOf(Quantity.class));
        Quantity quantity = (Quantity) dose;
        checkDosage(quantity);
    }
}
