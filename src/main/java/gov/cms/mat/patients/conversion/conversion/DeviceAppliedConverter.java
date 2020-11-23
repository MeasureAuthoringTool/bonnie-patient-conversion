package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DeviceAppliedConverter extends ConverterBase<Procedure> {
    public static final String QDM_TYPE = "QDM::DeviceApplied";

    public DeviceAppliedConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Procedure> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        Procedure procedure = new Procedure();
        procedure.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            procedure.setCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        procedure.setId(qdmDataElement.getId());

        if (qdmDataElement.getReason() != null) {
            procedure.getReasonCode().add(convertToCodeableConcept(qdmDataElement.getReason()));
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            procedure.setPerformed(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (qdmDataElement.getPerformer() != null) {
            log.info(UNEXPECTED_DATA_LOG_MESSAGE, QDM_TYPE, "performer");
            procedure.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getPerformer()));
        }

        if (!processNegation(qdmDataElement, procedure)) {
            procedure.setStatus(Procedure.ProcedureStatus.INPROGRESS);
        }

        return QdmToFhirConversionResult.<Procedure>builder()
                .fhirResource(procedure)
                .conversionMessages(conversionMessages)
                .build();
    }

    @Override
    void convertNegation(QdmDataElement qdmDataElement, Procedure procedure) {
        convertNegationProcedure(qdmDataElement, procedure);
    }
}
