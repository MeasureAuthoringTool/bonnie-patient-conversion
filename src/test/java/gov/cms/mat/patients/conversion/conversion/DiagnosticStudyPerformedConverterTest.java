package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.node.NullNode;
import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ObservationCommonTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest
@ActiveProfiles("test")
class DiagnosticStudyPerformedConverterTest extends BaseConversionTest implements FhirConversionTest, ObservationCommonTest {
    @Autowired
    private DiagnosticStudyPerformedConverter diagnosticStudyPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(DiagnosticStudyPerformedConverter.QDM_TYPE, diagnosticStudyPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhirWithoutNegation() {
        createObservationDataElement(qdmDataElement);

        qdmDataElement.setResult(NullNode.getInstance());

        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkWithoutNegationResult(result);
        assertNull(result.getFhirResource().getValue());
    }

    @Test
    void convertToFhirNegation() {
        createObservationDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        qdmDataElement.setResult(createCodeableConceptObjectNode());

        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkCodeableConceptObjectNode(result.getFhirResource().getValue());
    }



    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }
}