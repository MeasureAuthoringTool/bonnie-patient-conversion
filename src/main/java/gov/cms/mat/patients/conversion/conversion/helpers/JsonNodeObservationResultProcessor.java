package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonNodeObservationResultProcessor implements FhirCreator, DataElementFinder {
    private final CodeSystemEntriesService codeSystemEntriesService;
    private final List<String> conversionMessages;

    public JsonNodeObservationResultProcessor(CodeSystemEntriesService codeSystemEntriesService,
                                              List<String> conversionMessages) {
        this.codeSystemEntriesService = codeSystemEntriesService;
        this.conversionMessages = conversionMessages;
    }


    public Type findType(JsonNode result) {
        if (result instanceof ObjectNode) {
            return processObjectNode((ObjectNode) result);
        } else if (result instanceof NullNode) {
            log.trace("Null node ignored");
            return null;
        } else if (result instanceof TextNode) {
            return processTextNode((TextNode) result);
        } else if (result instanceof IntNode) {
            return processIntMode((IntNode) result);
        } else if (result instanceof DoubleNode) {
            var doubleNode = (DoubleNode) result;
            return new Quantity(doubleNode.doubleValue());
        } else {
            String message = "Observation result does not handle " + result.getClass().getSimpleName() + " value: " + result.asText();
            log.warn(message);
            conversionMessages.add(message);
            return null;
        }
    }

    private IntegerType processIntMode(IntNode result) {
        return new IntegerType(result.intValue());
    }

    Type processObjectNode(ObjectNode objectNode) {
        var type = searchForValueCodeableConcept(objectNode);

        if (type == null) {
            type = searchForValueQuantity(objectNode);
        }

        return type;
    }

    private Type searchForValueQuantity(ObjectNode objectNode) {
        JsonNode unitNode = objectNode.get("unit");
        JsonNode valueNode = objectNode.get("value");

        if (unitNode != null && valueNode != null) {
            return createQuantity(valueNode.asInt(), unitNode.asText());
        } else {
            return null;
        }
    }

    private Type searchForValueCodeableConcept(ObjectNode objectNode) {
        JsonNode codeNode = objectNode.get("code");
        JsonNode systemNode = objectNode.get("system"); // oid

        if (codeNode != null && systemNode != null) {
            var qdmCodeSystem = new QdmCodeSystem();
            qdmCodeSystem.setCode(codeNode.asText());
            qdmCodeSystem.setSystem(systemNode.asText());

            var displayNode = objectNode.get("display");

            if (displayNode != null) {
                qdmCodeSystem.setDisplay(displayNode.asText());
            }

            return convertToCodeableConcept(codeSystemEntriesService, qdmCodeSystem);
        } else {
            return null;
        }
    }

    private Type processTextNode(TextNode result) {
        String data = result.textValue();

        try {
            var localDateTime = LocalDateTime.parse(data, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            var date = convertToDateViaInstant(localDateTime);

            return new DateTimeType(date);
        } catch (Exception e) { // Not a date
            return new StringType(data);
        }
    }

    Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
