package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.TargetOutcome;
import org.hl7.fhir.r4.model.Goal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class CareCoalConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private CareCoalConverter careCoalConverter;

    @Test
    void getQdmType() {
        assertEquals(CareCoalConverter.QDM_TYPE, careCoalConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setTargetOutcome(createTargetOutcome());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setRelatedTo(List.of(createRelatedTo()));
        qdmDataElement.setPerformer(createPerformer());

        QdmToFhirConversionResult<Goal> result = careCoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkTargetOutCome(result.getFhirResource().getTargetFirstRep().getDetailCodeableConcept());
        checkRelevantPeriodGoal(result.getFhirResource().getStart(), result.getFhirResource().getStatusDateElement());
        checkRelatedTo(result.getFhirResource().getAddressesFirstRep());
        checkPerformer(result.getFhirResource().getExpressedBy());
    }

    @Test
    void convertToFhirEmptyTargetOutcome() {
        qdmDataElement.setTargetOutcome(new TargetOutcome());

        QdmToFhirConversionResult<Goal> result = careCoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertFalse(result.getFhirResource().hasTarget());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Goal> result = careCoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
    }
}