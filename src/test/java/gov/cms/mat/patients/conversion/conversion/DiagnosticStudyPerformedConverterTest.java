package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.node.NullNode;
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
import static org.junit.jupiter.api.Assertions.assertTrue;


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

        assertEquals(1, result.getConversionMessages().size());
        assertTrue( result.getConversionMessages().get(0).startsWith( "Cannot convert reason to basedOn."));
    }

    @Test
    void convertToFhirWithoutNegationUseRelevantDatetime() {
        createObservationDataElement(qdmDataElement);
        // if relevantPeriod & relevantDatetime both exist, period will be mapped
        qdmDataElement.setRelevantPeriod(null);

        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkRelevantDateTime(result.getFhirResource().getEffectiveDateTimeType().getValue());
    }


    @Test
    void convertToFhirNegation() {
        createObservationDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        qdmDataElement.setResult(createCodeableConceptObjectNode());

        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkCodeableConceptObjectNode(result.getFhirResource().getValue());
    }


    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Observation> result =
                diagnosticStudyPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }
}