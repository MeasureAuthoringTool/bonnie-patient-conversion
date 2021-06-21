package gov.cms.mat.patients.conversion.service.helpers;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RelatedToProcessor implements FhirCreator {
    private final FhirContext fhirContext;
    private final ObjectMapper objectMapper;
    private final List<FhirDataElement> fhirDataElements;

    public RelatedToProcessor(FhirContext fhirContext, ObjectMapper objectMapper, List<FhirDataElement> fhirDataElements) {
        this.fhirContext = fhirContext;
        this.objectMapper = objectMapper;
        this.fhirDataElements = fhirDataElements;
    }

    public void process() {
        processCommunications();
        processGoals();
    }

    private void processGoals() {
        var goalElementList = fhirDataElements.stream()
                .filter(fhirDataElement -> fhirDataElement.getFhirType().equals("Goal"))
                .filter(fhirDataElement -> ((Goal) fhirDataElement.getFhirObject()).hasAddresses())
                .collect(Collectors.toList());

        goalElementList.forEach(this::processGoalAddresses);
    }

    @SneakyThrows
    private void processGoalAddresses(FhirDataElement goalFhirDataElement) {
        var goal = (Goal) goalFhirDataElement.getFhirObject();

        goal.getAddresses().forEach(reference -> processBasedOn(reference, goalFhirDataElement));

        String json = toJson(fhirContext, goal);

        goalFhirDataElement.setFhirResource(objectMapper.readTree(json));
    }


    private void processCommunications() {
        var communicationElementList = fhirDataElements.stream()
                .filter(fhirDataElement -> fhirDataElement.getFhirType().equals("Communication"))
                .filter(fhirDataElement -> ((Communication) fhirDataElement.getFhirObject()).hasBasedOn())
                .collect(Collectors.toList());

        communicationElementList.forEach(this::processCommunicationRelatedTo);
    }

    @SneakyThrows
    private void processCommunicationRelatedTo(FhirDataElement communicationFhirDataElement) {
        var communication = (Communication) communicationFhirDataElement.getFhirObject();

        communication.getBasedOn().forEach(reference -> processBasedOn(reference, communicationFhirDataElement));

        String json = toJson(fhirContext, communication);

        communicationFhirDataElement.setFhirResource(objectMapper.readTree(json));
    }


    private void processBasedOn(Reference reference, FhirDataElement dataElement) {
        var splits = reference.getReference().split("/");

        if (splits.length != 2) {
            String message = "Cannot parse reference: " + reference.getReference();
            log.error(message);
            dataElement.getOutcome().getConversionMessages()
                    .add(message + ". Please adjust reference.");
        } else {
            String id = splits[1];

            var optional = fhirDataElements.stream()
                    .filter(f -> f.getFhirId().equals(id))
                    .findFirst();

            if (optional.isEmpty()) {
                String message = "Cannot find resource for relatedTo to with id: " + id;
                log.warn(message);

                dataElement.getOutcome().getConversionMessages()
                        .add(message + ". Please adjust reference.");
            } else {
                FhirDataElement relatedElement = optional.get();
                reference.setReference(relatedElement.getFhirType() + "/" + relatedElement.getFhirId());
            }
        }
    }
}
