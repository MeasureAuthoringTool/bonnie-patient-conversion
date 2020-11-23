package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.AdverseEvent;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class AdverseEventConverter extends ConverterBase<AdverseEvent> {
    public static final String QDM_TYPE = "QDM::AdverseEvent";

    public AdverseEventConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<AdverseEvent> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        AdverseEvent adverseEvent = new AdverseEvent();
        adverseEvent.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            adverseEvent.setEvent(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getType() != null) {
            adverseEvent.setCategory(Collections.singletonList(convertToCodeableConcept(qdmDataElement.getType())));
            log.info("We finally have category"); //no data
        }

        if (qdmDataElement.getSeverity() != null) {
            adverseEvent.setSeverity(convertToCodeableConcept(qdmDataElement.getSeverity()));
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "severity");
        }

        if (qdmDataElement.getRelevantDatetime() != null) {
            adverseEvent.setDate(qdmDataElement.getRelevantDatetime());
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getFacilityLocations())) {
            //no data
            // adverseEvent.setLocation(); // reference how to map
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "facilityLocations");
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            adverseEvent.setRecordedDate(qdmDataElement.getAuthorDatetime());
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "authorDatetime");
        }

        adverseEvent.setId(qdmDataElement.getId());

        // There is no Recorder attribute in qdmDataElement but could be mapped to one of  QdmPractitioner objects

        processNegation(qdmDataElement, adverseEvent);

        return QdmToFhirConversionResult.<AdverseEvent>builder()
                .fhirResource(adverseEvent)
                .conversionMessages(Collections.emptyList())
                .build();
    }
}
