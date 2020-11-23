package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SymptomConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private SymptomConverter symptomConverter;


    @Test
    void getQdmType() {
        assertEquals(SymptomConverter.QDM_TYPE, symptomConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setPrevalencePeriod(createPrevalencePeriod());
        qdmDataElement.setSeverity(createSeverity());

        QdmToFhirConversionResult<Observation> result = symptomConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkDataElementCodeableConcept(result.getFhirResource().getValueCodeableConcept());
        checkPrevalencePeriod(result.getFhirResource().getEffectivePeriod());
        checkSeverity(result.getFhirResource().getInterpretationFirstRep());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Observation> result = symptomConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
    }

}