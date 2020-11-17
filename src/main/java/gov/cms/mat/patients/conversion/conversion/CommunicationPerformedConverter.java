package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CommunicationPerformedConverter extends ConverterBase<Communication> {
    public static final String QDM_TYPE = "QDM::CommunicationPerformed";

    public CommunicationPerformedConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Communication> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();
        Communication communication = new Communication();
        communication.setId(qdmDataElement.getId());
        communication.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            communication.setStatusReason(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getCategory() != null) {
            communication.setCategory(List.of(convertToCodeableConcept( qdmDataElement.getCategory())));
        }
        if (qdmDataElement.getMedium() != null) {
            communication.setMedium(List.of(convertToCodeableConcept( qdmDataElement.getMedium())));
        }
        if (qdmDataElement.getSentDatetime() != null) {
            communication.setSent(qdmDataElement.getSentDatetime());
        }
        if (qdmDataElement.getReceivedDatetime() != null) {
            communication.setReceived(qdmDataElement.getReceivedDatetime());
        }
        if (CollectionUtils.isNotEmpty(qdmDataElement.getRelatedTo())) {
            communication.setBasedOn(convertRelatedTo(qdmDataElement.getRelatedTo()));
        }

        if (qdmDataElement.getSender() != null) {
            communication.setSender(createPractitionerReference(qdmDataElement.getSender()));
        }

        if (qdmDataElement.getRecipient() != null) {
            communication.setRecipient(List.of(createPractitionerReference(qdmDataElement.getRecipient())));
        }

        if (!processNegation(qdmDataElement, communication)) {
            communication.setStatus(Communication.CommunicationStatus.UNKNOWN);
            conversionMessages.add(NO_STATUS_MAPPING);
        }

        return QdmToFhirConversionResult.<Communication>builder()
                .fhirResource(communication)
                .conversionMessages(conversionMessages)
                .build();
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, Communication communication) {
        communication.setStatus(Communication.CommunicationStatus.NOTDONE);

        communication.setModifierExtension(List.of(createNotDoneExtension()));

        communication.setStatusReason(convertToCodeableConcept( qdmDataElement.getNegationRationale()));
    }
}
