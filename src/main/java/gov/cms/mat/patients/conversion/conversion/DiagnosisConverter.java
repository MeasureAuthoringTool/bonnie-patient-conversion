package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DiagnosisConverter extends ConverterBase<Condition> {
    public static final String QDM_TYPE = "QDM::Diagnosis";

    public DiagnosisConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Condition> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var condition = new Condition();
        condition.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            condition.setCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        condition.setId(qdmDataElement.getId());

        if (qdmDataElement.getPrevalencePeriod() != null) {
            if (qdmDataElement.getPrevalencePeriod().getLow() != null) {
                qdmDataElement.getPrevalencePeriod().getLow().setPrecision(TemporalPrecisionEnum.MILLI);
                condition.setOnset(qdmDataElement.getPrevalencePeriod().getLow());
            }

            if (qdmDataElement.getPrevalencePeriod().getHigh() != null) {
                qdmDataElement.getPrevalencePeriod().getHigh().setPrecision(TemporalPrecisionEnum.MILLI);
                condition.setAbatement(qdmDataElement.getPrevalencePeriod().getHigh());
            }
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            qdmDataElement.getAuthorDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            condition.setRecordedDateElement(qdmDataElement.getAuthorDatetime()); // usually comes in as null
        }

        if (qdmDataElement.getSeverity() != null) {
            condition.setSeverity(convertToCodeableConcept(qdmDataElement.getSeverity()));
        }

        if (qdmDataElement.getAnatomicalLocationSite() != null) {
            condition.setBodySite(List.of(convertToCodeableConcept(qdmDataElement.getAnatomicalLocationSite())));
        }

        condition.setClinicalStatus(createCodeableConcept(
                "http://hl7.org/fhir/ValueSet/condition-clinical",
                "active",
                "Active"));

        condition.setVerificationStatus(createCodeableConcept(
                "http://hl7.org/fhir/ValueSet/condition-ver-status",
                "confirmed",
                "Confirmed"));

        processNegation(qdmDataElement, condition);

        return QdmToFhirConversionResult.<Condition>builder()
                .fhirResource(condition)
                .conversionMessages(conversionMessages)
                .build();
    }
}
