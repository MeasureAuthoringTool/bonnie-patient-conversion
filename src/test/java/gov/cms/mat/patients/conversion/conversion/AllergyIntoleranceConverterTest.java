package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class AllergyIntoleranceConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private AllergyIntoleranceConverter allergyIntoleranceConverter;

    @Test
    void getQdmType() {
        assertEquals(AllergyIntoleranceConverter.QDM_TYPE, allergyIntoleranceConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setPrevalencePeriod(createPrevalencePeriod());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setSeverity(createSeverity());
        qdmDataElement.setType(createType());

        QdmToFhirConversionResult<AllergyIntolerance> result = allergyIntoleranceConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        checkDataElementCodeableConcept(result.getFhirResource().getCode());

        QdmPeriod qdmPeriod = createPrevalencePeriod();
        assertEquals(qdmPeriod.getLow(), result.getFhirResource().getOnsetDateTimeType().getValue());
        assertEquals(qdmPeriod.getHigh(), result.getFhirResource().getLastOccurrence());

        checkAuthorDatetime(result.getFhirResource().getRecordedDate());

        assertFalse(result.getFhirResource().getReactionFirstRep().hasSeverity()); // we cannot convert

        assertEquals(2, result.getConversionMessages().size());

        assertEquals("Active", result.getFhirResource().getClinicalStatus().getCoding().get(0).getDisplay());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<AllergyIntolerance> result = allergyIntoleranceConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        assertEquals(0, result.getConversionMessages().size());
    }
}