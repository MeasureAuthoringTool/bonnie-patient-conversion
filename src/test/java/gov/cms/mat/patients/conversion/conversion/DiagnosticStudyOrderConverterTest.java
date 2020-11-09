package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ServiceRequestCommonTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DiagnosticStudyOrderConverterTest extends BaseConversionTest implements FhirConversionTest, ServiceRequestCommonTest {
    private final ServiceRequest.ServiceRequestIntent FHIR_INTENT = ServiceRequest.ServiceRequestIntent.ORDER;

    @Autowired
    private DiagnosticStudyOrderConverter diagnosticStudyOrderConverter;

    @Test
    void getQdmType() {
        assertEquals(DiagnosticStudyOrderConverter.QDM_TYPE, diagnosticStudyOrderConverter.getQdmType());
    }

    @Test
    void convertToFhirWithoutNegation() {
        createServiceRequestDataElement(qdmDataElement);

        QdmToFhirConversionResult<ServiceRequest> result = diagnosticStudyOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkWithoutNegationResult(result, FHIR_INTENT);
    }

    @Test
    void convertToFhirNegation() {
        createServiceRequestDataElement(qdmDataElement);
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<ServiceRequest> result = diagnosticStudyOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result, FHIR_INTENT);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<ServiceRequest> result = diagnosticStudyOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(FHIR_INTENT, result.getFhirResource().getIntent());
    }
}