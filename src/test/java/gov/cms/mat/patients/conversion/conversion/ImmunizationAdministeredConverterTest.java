package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Immunization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class ImmunizationAdministeredConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private ImmunizationAdministeredConverter immunizationAdministeredConverter;

    @Test
    void getQdmType() {
        assertEquals(ImmunizationAdministeredConverter.QDM_TYPE, immunizationAdministeredConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setDosage(createDosage());
        qdmDataElement.setRoute(createRoute());
        qdmDataElement.setReason(createReason());
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());

        QdmToFhirConversionResult<Immunization> result = immunizationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        checkDataElementCodeableConcept(result.getFhirResource().getVaccineCode());
        checkDosage(result.getFhirResource().getDoseQuantity());
        checkRoute(result.getFhirResource().getRoute());
        checkRelevantDateTime(result.getFhirResource().getOccurrenceDateTimeType().getValue());

        assertEquals(Immunization.ImmunizationStatus.NULL, result.getFhirResource().getStatus());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals(NO_STATUS_MAPPING, result.getConversionMessages().get(0));
    }

    @Test
    void convertToFhirNegation() {
        // convert only processes if negation rational is absent
        qdmDataElement.setNegationRationale(createNegationRationale());
        assertNull(immunizationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement));
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Immunization> result = immunizationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        assertEquals(Immunization.ImmunizationStatus.NULL, result.getFhirResource().getStatus());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals(NO_STATUS_MAPPING, result.getConversionMessages().get(0));
    }
}