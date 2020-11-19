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
class MedicationOrderConverterTest extends BaseConversionTest implements FhirConversionTest, MedicationRequestTest {

    @Autowired
    private MedicationOrderConverter medicationOrderConverter;

    @Test
    void getQdmType() {
        assertEquals(MedicationOrderConverter.QDM_TYPE, medicationOrderConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        createMedicationRequestElement(qdmDataElement);

        QdmToFhirConversionResult<MedicationRequest> result = medicationOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertEquals(MedicationRequest.MedicationRequestStatus.UNKNOWN, result.getFhirResource().getStatus());

        assertEquals(1, result.getFhirResource().getReasonCode().size());

        checkMedicationRequest(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<MedicationRequest> result = medicationOrderConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertEquals(MedicationRequest.MedicationRequestStatus.UNKNOWN, result.getFhirResource().getStatus());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }
}