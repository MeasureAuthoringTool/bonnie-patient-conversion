package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class AllergyIntoleranceConverterTest implements FhirConversionTest {
    @Autowired
    private AllergyIntoleranceConverter allergyIntoleranceConverter;

    @Test
    void getQdmType() {
        assertEquals(AllergyIntoleranceConverter.QDM_TYPE, allergyIntoleranceConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createQdmDataElement();
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setPrevalencePeriod(createPrevalencePeriod());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setType(createType());

        QdmToFhirConversionResult<AllergyIntolerance> result = allergyIntoleranceConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        checkDataElementCode(result.getFhirResource().getCode());
        checkPrevalencePeriod(result.getFhirResource().getOnsetPeriod());
        checkAuthorDatetime(result.getFhirResource().getRecordedDate());
        checkType(result.getFhirResource().getReaction().get(0).getSubstance());
    }

    @Test
    void convertToFhirEmptyObjects() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createQdmDataElement();

        QdmToFhirConversionResult<AllergyIntolerance> result = allergyIntoleranceConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());
    }
}