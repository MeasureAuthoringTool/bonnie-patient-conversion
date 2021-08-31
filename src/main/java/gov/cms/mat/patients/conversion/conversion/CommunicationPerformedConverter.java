package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
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
public class CommunicationPerformedConverter extends ConverterBase<Communication> implements FhirCreator {
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
        var communication = new Communication();
        communication.setId(qdmDataElement.getId());
        communication.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            communication.setReasonCode(List.of(convertToCodeableConcept(qdmDataElement.getDataElementCodes())));
        }

        if (qdmDataElement.getCategory() != null) {
            communication.getCategory().add(convertToCodeableConcept(qdmDataElement.getCategory()));
        }

        if (qdmDataElement.getMedium() != null) {
            communication.getMedium().add(convertToCodeableConcept(qdmDataElement.getMedium()));
        }

        if (qdmDataElement.getSentDatetime() != null) {
            qdmDataElement.getSentDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            communication.setSentElement(qdmDataElement.getSentDatetime());
        }

        if (qdmDataElement.getReceivedDatetime() != null) {
            qdmDataElement.getReceivedDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            communication.setReceivedElement(qdmDataElement.getReceivedDatetime());
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getRelatedTo())) {
            communication.setBasedOn(convertRelatedTo(qdmDataElement.getRelatedTo()));
        }

        if (qdmDataElement.getSender() != null) {
            communication.setSender(createPractitionerReference(qdmDataElement.getSender()));
        }

        if (qdmDataElement.getRecipient() != null) {
            communication.getRecipient().add(createPractitionerReference(qdmDataElement.getRecipient()));
        }


        if (qdmDataElement.getAuthorDatetime() != null) {
            var recordedExtension = createRecordedExtension(qdmDataElement.getAuthorDatetime());
            communication.getExtension().add(recordedExtension);
        }

        if (!processNegation(qdmDataElement, communication)) {
            communication.setStatus(Communication.CommunicationStatus.COMPLETED);
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

        communication.getModifierExtension().add(createNotDoneExtension());

        communication.setStatusReason(convertToCodeableConcept(qdmDataElement.getNegationRationale()));
    }
}
