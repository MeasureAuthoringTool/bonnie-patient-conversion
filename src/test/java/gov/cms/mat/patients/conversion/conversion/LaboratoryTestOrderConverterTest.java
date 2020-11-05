package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ServiceRequestCommon;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LaboratoryTestOrderConverterTest extends BaseConversionTest implements FhirConversionTest, ServiceRequestCommon {
    private final ServiceRequest.ServiceRequestIntent FHIR_INTENT = ServiceRequest.ServiceRequestIntent.ORDER;

    @Autowired
    private LaboratoryTestOrderConverter laboratoryTestOrderConverter;

    @Test
    void getQdmType() {
        assertEquals(LaboratoryTestOrderConverter.QDM_TYPE, laboratoryTestOrderConverter.getQdmType());
    }

    @Test
    void convertToFhirWithoutNegation() {
        createServiceRequestDataElement(qdmDataElement);

        QdmToFhirConversionResult<ServiceRequest> result = laboratoryTestOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkWithoutNegationResult(result, FHIR_INTENT);
    }

    @Test
    void convertToFhirNegation() {
        createServiceRequestDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<ServiceRequest> result = laboratoryTestOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result, FHIR_INTENT);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<ServiceRequest> result = laboratoryTestOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(FHIR_INTENT, result.getFhirResource().getIntent());
    }
}