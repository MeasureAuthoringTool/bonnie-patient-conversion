package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmQuantity;
import gov.cms.mat.patients.conversion.dao.conversion.TargetOutcome;
import lombok.Data;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Ratio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CareGoalConverterTest extends BaseConversionTest implements FhirConversionTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CareGoalConverter careGoalConverter;

    @Test
    void getQdmType() {
        assertEquals(CareGoalConverter.QDM_TYPE, careGoalConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        TargetOutcome targetOutcome = createTargetOutcome();
        JsonNode node = objectMapper.valueToTree(targetOutcome);
        qdmDataElement.setTargetOutcome(node);

        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setRelatedTo(List.of(createRelatedTo()));
        qdmDataElement.setPerformer(createPerformer());

        qdmDataElement.setStatusDate(createStatusDate());

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkTargetOutCome(result.getFhirResource().getTargetFirstRep().getDetailCodeableConcept());

//        checkRelevantPeriodGoal(result.getFhirResource().getStartDateType(), result.getFhirResource().getTargetFirstRep().getDueDateType());

        checkRelatedTo(result.getFhirResource().getAddressesFirstRep());
        checkPerformer(result.getFhirResource().getExpressedBy());

        checkStatusDate(result.getFhirResource().getStatusDate());
    }

    @Test
    void convertToFhirBadTargetOutcome() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        TargetOutcome targetOutcome = createTargetOutcome();
        targetOutcome.setCode(null);
        JsonNode node = objectMapper.valueToTree(targetOutcome);
        qdmDataElement.setTargetOutcome(node);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals("Unable to convert TargetOutcome", result.getConversionMessages().get(0));
    }


    @Test
    void convertToFhirQuantity() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        QdmQuantity qdmQuantity = createSupply();
        qdmQuantity.setType("QDM::Quantity");
        JsonNode node = objectMapper.valueToTree(qdmQuantity);

        qdmDataElement.setTargetOutcome(node);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkSupply(result.getFhirResource().getTargetFirstRep().getDetailQuantity());
    }

    @SneakyThrows
    @Test
    void convertToFhirRatio() {
        QdmRatio qdmRatio = new QdmRatio();
        qdmRatio.set_type("System::Ratio"); // this tag could be wrong, what smes gave
        qdmRatio.setNumerator(createDosage());
        qdmRatio.setDenominator(createSupply());

        JsonNode node = objectMapper.valueToTree(qdmRatio);

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setTargetOutcome(node);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        Ratio ratio = result.getFhirResource().getTargetFirstRep().getDetailRatio();

        checkDosage(ratio.getNumerator());
        checkSupply(ratio.getDenominator());
    }

    @SneakyThrows
    @Test
    void convertToFhirRatioBadTag() {
        QdmRatio qdmRatio = new QdmRatio();
        qdmRatio.set_type("QDM::Ratio"); // this tag follows the pattern of other tags, cannot process and return null
        qdmRatio.setNumerator(createDosage());
        qdmRatio.setDenominator(createSupply());

        JsonNode node = objectMapper.valueToTree(qdmRatio);

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setTargetOutcome(node);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals("Unable to convert TargetOutcome", result.getConversionMessages().get(0));
    }

    @Test
    void convertToFhirBooleanTargetOutComeNotHandled() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        BooleanNode booleanNode = BooleanNode.FALSE; // fhir object Goal can take this we not handling this boolean type
        qdmDataElement.setTargetOutcome(booleanNode);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals("Unable to convert TargetOutcome", result.getConversionMessages().get(0));
    }

    @Test
    void convertToFhirIntTargetOutCome() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        IntNode intNode = new IntNode(123);
        qdmDataElement.setTargetOutcome(intNode);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(123, result.getFhirResource().getTargetFirstRep().getDetailIntegerType().getValue());
    }

    @Test
    void convertToFhirDoubleTargetOutCome() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        DoubleNode doubleNode = new DoubleNode(1.23);
        qdmDataElement.setTargetOutcome(doubleNode);

        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        // converts to string
        assertEquals("1.23", result.getFhirResource().getTargetFirstRep().getDetailStringType().getValue());
    }

    @Test
    void convertToFhirEmptyTargetOutcome() {
        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertFalse(result.getFhirResource().hasTarget());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Goal> result = careGoalConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
    }

    @Data
    private static class QdmRatio {
        String _type;
        QdmQuantity numerator;
        QdmQuantity denominator;
    }
}