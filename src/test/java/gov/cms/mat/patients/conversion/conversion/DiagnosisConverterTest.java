package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
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
        checkAnatomicalLocationSite(result.getFhirResource().getBodySiteFirstRep());

        assertEquals("Active", result.getFhirResource().getClinicalStatus().getCoding().get(0).getDisplay());
        assertEquals("Confirmed", result.getFhirResource().getVerificationStatus().getCoding().get(0).getDisplay());


    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Condition> result = diagnosisConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
    }
}