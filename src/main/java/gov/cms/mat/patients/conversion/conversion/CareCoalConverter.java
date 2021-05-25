package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CareCoalConverter extends ConverterBase<Goal> {
    public static final String QDM_TYPE = "QDM::CareGoal";

    public CareCoalConverter(CodeSystemEntriesService codeSystemEntriesService,
                             FhirContext fhirContext,
                             ObjectMapper objectMapper,
                             ValidationService validationService) {
        super(codeSystemEntriesService, fhirContext, objectMapper, validationService);
    }

    @Override
    public String getQdmType() {
        return QDM_TYPE;
    }

    @Override
    public QdmToFhirConversionResult<Goal> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();
        var goal = new Goal();
        goal.setSubject(createPatientReference(fhirPatient));
        goal.setId(qdmDataElement.getId());

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            goal.getTargetFirstRep().setMeasure(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getTargetOutcome() != null) {
            var type = processTargetOutcome(qdmDataElement.getTargetOutcome());

            if (type != null) {
                goal.getTargetFirstRep().setDetail(type);
            } else {
                conversionMessages.add("Unable to convert TargetOutcome");
            }

        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            goal.setStart(new DateType(qdmDataElement.getRelevantPeriod().getLow()));
            goal.getTargetFirstRep().setDue(new DateType(qdmDataElement.getRelevantPeriod().getHigh()));
        }

        if (qdmDataElement.getProcessedStatusDate() != null) {
            goal.setStatusDate(qdmDataElement.getProcessedStatusDate());
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getRelatedTo())) {
            goal.setAddresses(convertRelatedTo(qdmDataElement.getRelatedTo()));
        }

        if (qdmDataElement.getPerformer() != null) {
            goal.setExpressedBy(createPractitionerReference(qdmDataElement.getPerformer()));
        }

        processNegation(qdmDataElement, goal); // no negations expected

        return QdmToFhirConversionResult.<Goal>builder()
                .fhirResource(goal)
                .conversionMessages(conversionMessages)
                .build();
    }

    private Type processTargetOutcome(JsonNode jsonNode) {
        log.debug(jsonNode.getClass().getName());

        if (jsonNode instanceof IntNode) {
            var intNode = (IntNode) jsonNode;
            return new IntegerType(intNode.intValue());
        }

        if (jsonNode instanceof DoubleNode) {
            var doubleNode = (DoubleNode) jsonNode;
            return new StringType(doubleNode.asText());
        }

        if (jsonNode instanceof ObjectNode) {
            return processObjectNode((ObjectNode) jsonNode);
        }

        return null;
    }


    private Type processObjectNode(ObjectNode objectNode) {
        JsonNode typeNode = objectNode.get("_type");

        if (typeNode == null) {
            var qdmCodeSystem = convertToQdmCodeSystem(objectNode);

            if (qdmCodeSystem == null) {
                return null;
            } else {
                return convertToCodeableConcept(qdmCodeSystem);
            }
        } else {
            String type = typeNode.asText();

            if (type.equals("QDM::Quantity")) {
                return createQuantity(objectNode);
            }

            if (type.equals("System::Ratio")) {
                return createRatio(objectNode);
            }

            return null;
        }
    }

    private Ratio createRatio(ObjectNode objectNode) {
        return new Ratio()
                .setNumerator(createQuantity((ObjectNode) objectNode.get("numerator")))
                .setDenominator(createQuantity((ObjectNode) objectNode.get("denominator")));
    }

    private Quantity createQuantity(ObjectNode objectNode) {
        return new Quantity()
                .setSystem(UCUM_SYSTEM)
                .setCode(objectNode.get("unit").asText())
                .setValue(objectNode.get("value").asInt());
    }

    private QdmCodeSystem convertToQdmCodeSystem(ObjectNode objectNode) {
        JsonNode systemNode = objectNode.get("system");
        JsonNode codeNode = objectNode.get("code");

        if (systemNode == null || codeNode == null ||
                systemNode.asText().equals("null") ||
                codeNode.asText().equals("null")) {
            log.debug("Cannot find system or code in objectNode: {} ", objectNode.toPrettyString());
            return null;
        }

        JsonNode displayNode = objectNode.get("display");

        var qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem(systemNode.asText());
        qdmCodeSystem.setCode(codeNode.asText());

        if (displayNode != null) {
            qdmCodeSystem.setDisplay(displayNode.asText());
        }

        return qdmCodeSystem;
    }
}
