package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Coverage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ParticipationConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private ParticipationConverter participationConverter;

    @Test
    void getQdmType() {
        assertEquals(ParticipationConverter.QDM_TYPE, participationConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setParticipationPeriod(createPrevalencePeriod());

        QdmToFhirConversionResult<Coverage> result = participationConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getBeneficiary());

        checkDataElementCodeableConcept(result.getFhirResource().getType());
        checkPrevalencePeriod(result.getFhirResource().getPeriod());

        assertEquals(Coverage.CoverageStatus.ACTIVE, result.getFhirResource().getStatus());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Coverage> result = participationConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getBeneficiary());

        assertEquals(Coverage.CoverageStatus.ACTIVE, result.getFhirResource().getStatus());
    }
}