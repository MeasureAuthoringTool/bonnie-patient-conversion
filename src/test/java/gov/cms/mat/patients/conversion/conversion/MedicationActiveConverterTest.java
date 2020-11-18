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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class MedicationActiveConverterTest extends BaseConversionTest implements FhirConversionTest, MedicationRequestTest {
    @Autowired
    private MedicationActiveConverter medicationActiveConverter;

    @Test
    void getQdmType() {
        assertEquals(MedicationActiveConverter.QDM_TYPE, medicationActiveConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        createMedicationRequestElement(qdmDataElement);

        QdmToFhirConversionResult<MedicationRequest> result = medicationActiveConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertEquals(MedicationRequest.MedicationRequestStatus.ACTIVE, result.getFhirResource().getStatus());


        assertEquals(1, result.getFhirResource().getReasonCode().size());

        checkMedicationRequest(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<MedicationRequest> result = medicationActiveConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertEquals(MedicationRequest.MedicationRequestStatus.ACTIVE, result.getFhirResource().getStatus());

        assertEquals(0, result.getConversionMessages().size());
    }
}