package gov.cms.mat.patients.conversion.service.helpers;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.dao.results.ConversionOutcome;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RelatedToProcessorTest implements FhirConversionTest {
    @Autowired
    private FhirContext fhirContext;
    @Autowired
    private ObjectMapper objectMapper;

    private List<FhirDataElement> dataElements;
    private Practitioner practitioner;

    @BeforeEach
    void setUp() {
        dataElements = new ArrayList<>();

        practitioner = new Practitioner();
        practitioner.setId("PRACTITIONER");

        dataElements.add(FhirDataElement.builder()
                .fhirType(practitioner.fhirType())
                .fhirId(practitioner.getId())
                .fhirObject(practitioner)
                .build());
    }

    @Test
    void process() {
        dataElements.clear();

        RelatedToProcessor relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, dataElements);
        relatedToProcessor.process();

        assertTrue(dataElements.isEmpty());
    }

    @Test
    void processGoal() {

        Goal goal = new Goal();
        goal.setId("GOAL");
        // the qdm data only has a id we dont know type
        // we will try find later when all processing done
        Reference reference = new Reference("Unknown/" + practitioner.getId());
        goal.setAddresses(List.of(reference));

        dataElements.add(FhirDataElement.builder()
                .fhirType(goal.fhirType())
                .fhirId(goal.getId())
                .fhirObject(goal)
                .build());

        RelatedToProcessor relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, dataElements);
        relatedToProcessor.process();

        assertEquals("Practitioner/PRACTITIONER", goal.getAddressesFirstRep().getReference());
    }

    @Test
    void processCommunication() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId("PRACTITIONER");

        dataElements.add(FhirDataElement.builder()
                .fhirType(practitioner.fhirType())
                .fhirId(practitioner.getId())
                .fhirObject(practitioner)
                .build());

        Communication communication = new Communication();
        communication.setId("COMMUNICATION");
        // the qdm data only has a id we dont know type
        // we will try find later when all processing done
        Reference reference = new Reference("Unknown/" + practitioner.getId());
        communication.setBasedOn(List.of(reference));

        dataElements.add(FhirDataElement.builder()
                .fhirType(communication.fhirType())
                .fhirId(communication.getId())
                .fhirObject(communication)
                .build());

        RelatedToProcessor relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, dataElements);
        relatedToProcessor.process();

        assertEquals("Practitioner/PRACTITIONER", communication.getBasedOnFirstRep().getReference());
    }

    @Test
    void processCommunicationBadReference() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId("PRACTITIONER");

        dataElements.add(FhirDataElement.builder()
                .fhirType(practitioner.fhirType())
                .fhirId(practitioner.getId())
                .fhirObject(practitioner)
                .build());

        Communication communication = new Communication();
        communication.setId("COMMUNICATION");
        // the qdm data only has a id we dont know type
        // we will try find later when all processing done
        Reference reference = new Reference("Unknown/" + practitioner.getId() + "/WHYAMIHERE");
        communication.setBasedOn(List.of(reference));

        ConversionOutcome outcome = ConversionOutcome.builder()
                .conversionMessages(new ArrayList<>())
                .build();

        dataElements.add(FhirDataElement.builder()
                .fhirType(communication.fhirType())
                .fhirId(communication.getId())
                .fhirObject(communication)
                .outcome(outcome)
                .build());

        RelatedToProcessor relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, dataElements);
        relatedToProcessor.process();

        // did not change
        assertEquals("Unknown/PRACTITIONER/WHYAMIHERE", communication.getBasedOnFirstRep().getReference());
        assertEquals(1, outcome.getConversionMessages().size());
        assertTrue(outcome.getConversionMessages().get(0).startsWith("Cannot parse reference:"));
    }

    @Test
    void processGoalReferenceNotFound() {
        Goal goal = new Goal();
        goal.setId("GOAL");
        // the qdm data only has a id we dont know type
        // we will try find later when all processing done
        Reference reference = new Reference("Unknown/" + practitioner.getId() + "_BAD_BIT");
        goal.setAddresses(List.of(reference));

        ConversionOutcome outcome = ConversionOutcome.builder()
                .conversionMessages(new ArrayList<>())
                .build();

        dataElements.add(FhirDataElement.builder()
                .fhirType(goal.fhirType())
                .fhirId(goal.getId())
                .fhirObject(goal)
                .outcome(outcome)
                .build());

        RelatedToProcessor relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, dataElements);
        relatedToProcessor.process();

        assertEquals("Unknown/PRACTITIONER_BAD_BIT", goal.getAddressesFirstRep().getReference());
        assertEquals(1, outcome.getConversionMessages().size());
        assertTrue(outcome.getConversionMessages().get(0).startsWith("Cannot find resource for relatedTo to with id:"));
    }
}