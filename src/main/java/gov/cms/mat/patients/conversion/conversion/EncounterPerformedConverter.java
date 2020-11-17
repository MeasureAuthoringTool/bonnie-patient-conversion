package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.Diagnoses;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EncounterPerformedConverter extends ConverterBase<Encounter> {
    public static final String NEGATION_MESSAGE =
            "There is no current use case for an eCQM to request a reason for failure to perform an encounter.";
    public static final String QDM_TYPE = "QDM::EncounterPerformed";

    public EncounterPerformedConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Encounter> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();
        Encounter encounter = new Encounter();
        encounter.setSubject(createPatientReference(fhirPatient));
        encounter.setId(qdmDataElement.getId());

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            encounter.setClass_(convertToCoding(qdmDataElement.getDataElementCodes()));
        }

        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8114-encounter-performed
        // 	consider constraint to - arrived, triaged, in-progress, on-leave, finished
        encounter.setStatus(Encounter.EncounterStatus.UNKNOWN);
        conversionMessages.add(NO_STATUS_MAPPING);


        if (qdmDataElement.getRelevantPeriod() != null) {
            encounter.setPeriod(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDiagnoses())) {
            encounter.setDiagnosis(createDiagnoses(qdmDataElement.getDiagnoses()));
        }

        if (qdmDataElement.getLengthOfStay() != null) {
            Duration duration = new Duration();
            duration.setUnit(qdmDataElement.getLengthOfStay().getUnit());
            duration.setValue(qdmDataElement.getLengthOfStay().getValue());
            encounter.setLength(duration);
        }

        if (qdmDataElement.getDischargeDisposition() != null) {
            Encounter.EncounterHospitalizationComponent hospitalizationComponent = encounter.getHospitalization();
            CodeableConcept codeableConcept = convertToCodeableConcept(qdmDataElement.getDischargeDisposition());
            hospitalizationComponent.setDischargeDisposition(codeableConcept);
        }

        if (processNegation(qdmDataElement, encounter)) {
            // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8114-encounter-performed
            //	There is no current use case for an eCQM to request a reason for failure to perform an encounter.
            conversionMessages.add(NEGATION_MESSAGE);
        }

        return QdmToFhirConversionResult.<Encounter>builder()
                .fhirResource(encounter)
                .conversionMessages(conversionMessages)
                .build();
    }


    private List<Encounter.DiagnosisComponent> createDiagnoses(List<Diagnoses> diagnoses) {
        return diagnoses.stream()
                .map(this::createDiagnosis)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Encounter.DiagnosisComponent createDiagnosis(Diagnoses diagnoses) {

        Encounter.DiagnosisComponent diagnosisComponent = new Encounter.DiagnosisComponent();

        try {
            CodeSystemEntry codeSystemEntry = getCodeSystemEntriesService().findRequired(diagnoses.getCode().getSystem());
            diagnosisComponent.setUse(createCodeableConcept(diagnoses.getCode(), codeSystemEntry.getUrl()));
        } catch (Exception e) {
            if (diagnoses.getCode() == null) {
                log.warn("Diagnoses does not contain a code: {}", diagnoses);

                return null;
            } else {
                diagnosisComponent.setUse(createCodeableConcept(diagnoses.getCode(),
                        "urn:oid:" + diagnoses.getCode().getCodeSystem()));
            }
        }


        //  We are not doing references to other qdmElements at this juncture
        //   "message": "Profile http://hl7.org/fhir/StructureDefinition/Encounter,
        //   Element 'Encounter.diagnosis[0].condition': minimum required = 1, but only found 0",


        if (diagnoses.getRank() != null) {
            diagnosisComponent.setRank(diagnoses.getRank());
        }

        return diagnosisComponent;
    }
}
