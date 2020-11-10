package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.TargetOutcome;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Patient;
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
        Goal goal = new Goal();
        goal.setSubject(createPatientReference(fhirPatient));

        goal.getTargetFirstRep().setMeasure(convertToCodeSystems(codeSystemEntriesService, qdmDataElement.getDataElementCodes()));

        goal.setId(qdmDataElement.getId());

        if (qdmDataElement.getTargetOutcome() != null) {
            QdmCodeSystem qdmCodeSystem = convertToQdmCodeSystem(qdmDataElement.getTargetOutcome());

            if (qdmCodeSystem != null) {
                goal.getTargetFirstRep().setDetail(convertToCodeableConcept(codeSystemEntriesService, qdmCodeSystem));
            }
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            goal.setStart(new DateType(qdmDataElement.getRelevantPeriod().getLow()));
            goal.setStatusDate(qdmDataElement.getRelevantPeriod().getHigh());
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

    private QdmCodeSystem convertToQdmCodeSystem(TargetOutcome targetOutcome) {
        if (targetOutcome == null) {
            log.warn("targetOutcome is null");
            return null;
        } else if (StringUtils.isBlank(targetOutcome.getSystem()) || StringUtils.isBlank(targetOutcome.getCode())) {
            log.warn("targetOutcome is invalid: {}", targetOutcome);
            return null;
        } else {
            QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
            qdmCodeSystem.setSystem(targetOutcome.getSystem());
            qdmCodeSystem.setCode(targetOutcome.getCode());
            qdmCodeSystem.setDisplay(targetOutcome.getDisplay());
            return qdmCodeSystem;
        }
    }
}
