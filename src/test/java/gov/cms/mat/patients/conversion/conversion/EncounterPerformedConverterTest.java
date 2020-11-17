package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.Diagnoses;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import org.hl7.fhir.r4.model.Encounter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static gov.cms.mat.patients.conversion.conversion.EncounterPerformedConverter.NEGATION_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class EncounterPerformedConverterTest extends BaseConversionTest implements FhirConversionTest {

    @Autowired
    private EncounterPerformedConverter encounterPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(EncounterPerformedConverter.QDM_TYPE, encounterPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setDiagnoses(List.of(createDiagnose()));
        qdmDataElement.setLengthOfStay(createLengthOfStay());
        qdmDataElement.setDischargeDisposition(createDischargeDisposition());

        QdmToFhirConversionResult<Encounter> result = encounterPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkDataElementCoding(result.getFhirResource().getClass_());
        checkDiagnose(result.getFhirResource().getDiagnosisFirstRep());
        checkLengthOfStay(result.getFhirResource().getLength());
        checkDischargeDisposition(result.getFhirResource().getHospitalization());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals(NO_STATUS_MAPPING, result.getConversionMessages().get(0));
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<Encounter> result = encounterPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(2, result.getConversionMessages().size());
        assertEquals(NO_STATUS_MAPPING, result.getConversionMessages().get(0));
        assertEquals(NEGATION_MESSAGE, result.getConversionMessages().get(1));
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Encounter> result = encounterPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(1, result.getConversionMessages().size());
        assertEquals(NO_STATUS_MAPPING, result.getConversionMessages().get(0));
    }

    private void checkDischargeDisposition(Encounter.EncounterHospitalizationComponent hospitalization) {
        checkDischargeDisposition(hospitalization.getDischargeDisposition());
    }

    private void checkDiagnose(Encounter.DiagnosisComponent diagnosisComponent) {
        assertEquals(22, diagnosisComponent.getRank());

        checkSNOMEDCodeableConcept(diagnosisComponent.getUse(), "238484001", "Tennis toe");
    }

    private Diagnoses createDiagnose() {
        Diagnoses diagnoses = new Diagnoses();

        QdmCodeSystem code = createSNOMEDCode("238484001", "Tennis toe");
        diagnoses.setCode(code);

        diagnoses.setRank(22);

        return diagnoses;
    }
}