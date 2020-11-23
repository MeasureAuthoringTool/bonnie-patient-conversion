package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Timing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SubstanceAdministeredConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private SubstanceAdministeredConverter substanceAdministeredConverter;

    @Test
    void getQdmType() {
        assertEquals(SubstanceAdministeredConverter.QDM_TYPE, substanceAdministeredConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setFrequency(createFrequency()); //  not mapped
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setRoute(createRoute());  //  not mapped
        qdmDataElement.setDosage(createDosage());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setRefills(33);

        QdmToFhirConversionResult<NutritionOrder> result = substanceAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        // DataElementCodes mapped twice
        checkDataElementCodeableConcept(result.getFhirResource().getOralDiet().getTypeFirstRep());

        checkAuthorDatetime(result.getFhirResource().getDateTime());

        // DataElementCodes mapped twice
        checkDataElementCodeableConcept(result.getFhirResource().getEnteralFormula().getBaseFormulaType());

        checkDosage(result.getFhirResource().getEnteralFormula().getAdministrationFirstRep().getQuantity());

        checkRelevantPeriodTiming(result.getFhirResource().getEnteralFormula().getAdministrationFirstRep().getSchedule());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }

    private void checkRelevantPeriodTiming(Timing schedule) {
        assertEquals(createRelevantPeriod().getLow(), schedule.getEvent().get(0).getValue());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<NutritionOrder> result = substanceAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        assertEquals(NutritionOrder.NutritionOrderStatus.UNKNOWN, result.getFhirResource().getStatus());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }
}