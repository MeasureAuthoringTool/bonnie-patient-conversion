package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class DiagnosisConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private DiagnosisConverter diagnosisConverter;

    @Test
    void getQdmType() {
        assertEquals(DiagnosisConverter.QDM_TYPE, diagnosisConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setPrevalencePeriod(createPrevalencePeriod());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setSeverity(createSeverity());
        qdmDataElement.setAnatomicalLocationSite(createAnatomicalLocationSite());


        QdmToFhirConversionResult<Condition> result = diagnosisConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
        checkSeverity(result.getFhirResource().getSeverity());
        checkAuthorDatetime(result.getFhirResource().getRecordedDate());
        checkPrevalencePeriod(result.getFhirResource().getOnsetPeriod());
        checkAnatomicalLocationSite( result.getFhirResource().getBodySiteFirstRep());
    }

    private void checkAnatomicalLocationSite(CodeableConcept codeableConcept) {
        assertEquals("431321000124109",  codeableConcept.getCodingFirstRep().getCode());
        assertEquals("Body Site Value Set", codeableConcept.getCodingFirstRep().getDisplay());
        assertEquals("http://snomed.info/sct", codeableConcept.getCodingFirstRep().getSystem());
    }

    private QdmCodeSystem createAnatomicalLocationSite() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setDisplay("Body Site Value Set");
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("431321000124109");
        return qdmCodeSystem;
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Condition> result = diagnosisConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
    }
}