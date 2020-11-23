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
class PatientCharacteristicPayerConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private PatientCharacteristicPayerConverter patientCharacteristicPayerConverter;

    @Test
    void getQdmType() {
        assertEquals(PatientCharacteristicPayerConverter.QDM_TYPE, patientCharacteristicPayerConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());

        QdmToFhirConversionResult<Coverage> result = patientCharacteristicPayerConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getBeneficiary());

        checkDataElementCodeableConcept(result.getFhirResource().getType());
        checkRelevantPeriod(result.getFhirResource().getPeriod());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Coverage> result = patientCharacteristicPayerConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getBeneficiary());
    }
}