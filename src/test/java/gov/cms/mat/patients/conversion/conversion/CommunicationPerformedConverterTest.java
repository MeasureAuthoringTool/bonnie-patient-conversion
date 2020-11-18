package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Communication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class CommunicationPerformedConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private CommunicationPerformedConverter communicationPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(CommunicationPerformedConverter.QDM_TYPE, communicationPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setCategory(createCategory());
        qdmDataElement.setMedium(createMedium());

        qdmDataElement.setSentDatetime(createSentDatetime());
        qdmDataElement.setReceivedDatetime(createReceivedDatetime());
        qdmDataElement.setRelatedTo(List.of(createRelatedTo()));
        qdmDataElement.setSender(createSender());
        qdmDataElement.setRecipient(createRecipient());

        QdmToFhirConversionResult<Communication> result = communicationPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Communication.CommunicationStatus.UNKNOWN, result.getFhirResource().getStatus());

        checkNoStatusMappingOnly(result.getConversionMessages());

        checkDataElementCodeableConcept(result.getFhirResource().getStatusReason());

        checkMedium(result.getFhirResource().getMediumFirstRep());
        checkSentDatetime(result.getFhirResource().getSent());
        checkReceivedDatetime(result.getFhirResource().getReceived());
        checkRelatedTo(result.getFhirResource().getBasedOnFirstRep());
        checkSender(result.getFhirResource().getSender());
        checkRecipient(result.getFhirResource().getRecipientFirstRep());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<Communication> result = communicationPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Communication.CommunicationStatus.NOTDONE, result.getFhirResource().getStatus());
        assertEquals(0, result.getConversionMessages().size());

        checkNegationRationaleTypeCodeableConcept(result.getFhirResource().getStatusReason());

        assertEquals(1, result.getFhirResource().getModifierExtension().size());
        checkNotDoneExtension(result.getFhirResource().getModifierExtension().get(0));
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Communication> result = communicationPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }
}