package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_DO_NOT_PERFORM_REASON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AssessmentOrderConverterTest extends BaseConverterTest {
    @Autowired
    private AssessmentOrderConverter assessmentOrderConverter;

    @Test
    void getQdmType() {
        assertEquals(AssessmentOrderConverter.QDM_TYPE, assessmentOrderConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createDataElement();

        QdmToFhirConversionResult<ServiceRequest> result = assessmentOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkDataElement(result);
        assertEquals(ServiceRequest.ServiceRequestStatus.UNKNOWN, result.getFhirResource().getStatus());

        assertEquals(ServiceRequest.ServiceRequestIntent.ORDER, result.getFhirResource().getIntent());

        assertFalse(result.getFhirResource().getDoNotPerform());
    }

    @Test
    void convertToFhirNegation() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createDataElement();
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<ServiceRequest> result = assessmentOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkDataElement(result);
        assertEquals(ServiceRequest.ServiceRequestStatus.COMPLETED, result.getFhirResource().getStatus());
        assertEquals(ServiceRequest.ServiceRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertTrue(result.getFhirResource().getDoNotPerform());

        assertEquals(1, result.getFhirResource().getExtension().size());
        assertEquals(QICORE_DO_NOT_PERFORM_REASON, result.getFhirResource().getExtension().get(0).getUrl());
        checkNegationRationale(result.getFhirResource().getExtension().get(0).getValue());

    }


    @Test
    void convertToFhirEmptyObjects() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createQdmDataElement();

        QdmToFhirConversionResult<ServiceRequest> result = assessmentOrderConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(ServiceRequest.ServiceRequestIntent.ORDER, result.getFhirResource().getIntent());
    }

    private void checkDataElement(QdmToFhirConversionResult<ServiceRequest> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        checkDataElementCode(result.getFhirResource().getCode());
        checkAuthorDatetime(result.getFhirResource().getAuthoredOn());
        checkReason(result.getFhirResource().getReasonCodeFirstRep());
    }

    private QdmDataElement createDataElement() {
        QdmDataElement qdmDataElement = createQdmDataElement();
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setReason(createReason());
        return qdmDataElement;
    }
}