package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.Diagnoses;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
        var encounter = new Encounter();
        encounter.setSubject(createPatientReference(fhirPatient));
        encounter.setId(qdmDataElement.getId());

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            encounter.setType(List.of(convertToCodeableConcept(qdmDataElement.getDataElementCodes())));
        }

        encounter.setStatus(Encounter.EncounterStatus.FINISHED);

        if (qdmDataElement.getRelevantPeriod() != null) {
            encounter.setPeriod(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDiagnoses())) {
            encounter.setDiagnosis(createDiagnoses(qdmDataElement.getDiagnoses()));
        }

        if (qdmDataElement.getLengthOfStay() != null) {
            var duration = new Duration();
            duration.setUnit(qdmDataElement.getLengthOfStay().getUnit());
            duration.setValue(qdmDataElement.getLengthOfStay().getValue());
            encounter.setLength(duration);
        }

        if (qdmDataElement.getDischargeDisposition() != null) {
            Encounter.EncounterHospitalizationComponent hospitalizationComponent = encounter.getHospitalization();
            var codeableConcept = convertToCodeableConcept(qdmDataElement.getDischargeDisposition());
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

        var diagnosisComponent = new Encounter.DiagnosisComponent();

        try {
            var codeSystemEntry = getCodeSystemEntriesService().findRequired(diagnoses.getCode().getSystem());
            diagnosisComponent.setUse(createCodeableConcept(diagnoses.getCode(), codeSystemEntry.getUrl()));
        } catch (Exception e) {
            if (diagnoses.getCode() == null) {
                log.warn("Diagnoses does not contain a code: {}", diagnoses);

                return null;
            } else {
                diagnosisComponent.setUse(createCodeableConcept(diagnoses.getCode(),
                        "urn:oid:" + diagnoses.getCode().getSystem()));
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
