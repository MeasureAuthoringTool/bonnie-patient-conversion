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

@SpringBootTest
@ActiveProfiles("test")
class AssessmentPerformedConverterTest extends BaseConversionTest implements FhirConversionTest, ObservationCommonTest {
    @Autowired
    private AssessmentPerformedConverter assessmentPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(AssessmentPerformedConverter.QDM_TYPE, assessmentPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhirWithoutNegation() {
        createObservationDataElement(qdmDataElement);

        qdmDataElement.setResult(createIntegerTypeResult());

        QdmToFhirConversionResult<Observation> result = assessmentPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkWithoutNegationResult(result);

        checkIntegerTypeResult(result.getFhirResource().getValue());
    }

    @Test
    void convertToFhirNegation() {
        createObservationDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        qdmDataElement.setResult(createTextTypeResultDate());

        QdmToFhirConversionResult<Observation> result = assessmentPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegationResult(result);

        checkTextTypeResultDate(result.getFhirResource().getValue());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Observation> result = assessmentPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }
}