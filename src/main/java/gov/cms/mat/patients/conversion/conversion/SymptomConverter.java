package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SymptomConverter extends ConverterBase<Observation> {
    public static final String QDM_TYPE = "QDM::Symptom";

    public SymptomConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Observation> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#822-symptom
        List<String> conversionMessages = new ArrayList<>();

        var observation = new Observation();
        observation.setSubject(createPatientReference(fhirPatient));
        conversionMessages.add(NO_STATUS_MAPPING);

        observation.setStatus(Observation.ObservationStatus.UNKNOWN);

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            observation.setValue(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        observation.setId(qdmDataElement.getId());

        if (qdmDataElement.getSeverity() != null) {
            observation.addInterpretation(convertToCodeableConcept(qdmDataElement.getSeverity()));
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "severity");
        }

        if (qdmDataElement.getPrevalencePeriod() != null) {
            observation.setEffective(convertPeriod(qdmDataElement.getPrevalencePeriod()));
        }

        processNegation(qdmDataElement, observation);

        return QdmToFhirConversionResult.<Observation>builder()
                .fhirResource(observation)
                .conversionMessages(conversionMessages)
                .build();
    }
}
