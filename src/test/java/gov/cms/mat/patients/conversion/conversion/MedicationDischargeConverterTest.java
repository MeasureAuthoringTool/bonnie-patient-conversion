package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.MedicationRequestTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class MedicationDischargeConverterTest extends BaseConversionTest implements FhirConversionTest, MedicationRequestTest {
    @Autowired
    private MedicationDischargeConverter medicationDischargeConverter;

    @Test
    void getQdmType() {
        assertEquals(MedicationDischargeConverter.QDM_TYPE, medicationDischargeConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        createMedicationRequestElement(qdmDataElement);

        QdmToFhirConversionResult<MedicationRequest> result = medicationDischargeConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertNull(result.getFhirResource().getIntent());
        assertNull(result.getFhirResource().getStatus());

        assertEquals(1, result.getFhirResource().getReasonCode().size());

        checkMedicationRequest(result);
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setReason(createReason());
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<MedicationRequest> result = medicationDischargeConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertEquals(2, result.getFhirResource().getReasonCode().size()); // reason & negation in that order

        checkNegation(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<MedicationRequest> result = medicationDischargeConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertNull(result.getFhirResource().getIntent());
        assertNull(result.getFhirResource().getStatus());

        assertEquals(0, result.getConversionMessages().size());
    }
}