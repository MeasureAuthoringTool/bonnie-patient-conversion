package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ObservationCommonTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class PhysicalExamPerformedConverterTest extends BaseConversionTest implements FhirConversionTest, ObservationCommonTest {
    @Autowired
    private PhysicalExamPerformedConverter physicalExamPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(PhysicalExamPerformedConverter.QDM_TYPE, physicalExamPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhirWithoutNegation() {
        createObservationDataElement(qdmDataElement);

        qdmDataElement.setResult(null);

        QdmToFhirConversionResult<Observation> result = physicalExamPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkWithoutNegationResult(result);

        assertNull(result.getFhirResource().getValue());
    }

    @Test
    void convertToFhirNegation() {
        createObservationDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        qdmDataElement.setResult(createTextTypeResult());

        QdmToFhirConversionResult<Observation> result = physicalExamPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkNegationResult(result);

        checkTextTypeResult(result.getFhirResource().getValue());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Observation> result = physicalExamPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }

}