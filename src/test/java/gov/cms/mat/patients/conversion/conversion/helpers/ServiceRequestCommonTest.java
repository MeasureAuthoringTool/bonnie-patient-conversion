package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.ServiceRequest;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_DO_NOT_PERFORM_REASON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface ServiceRequestCommonTest extends FhirConversionTest {
    default void checkDataElement(QdmToFhirConversionResult<ServiceRequest> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        checkDataElementCodeableConcept(result.getFhirResource().getCode());
        checkAuthorDatetime(result.getFhirResource().getAuthoredOn());
        checkReason(result.getFhirResource().getReasonCodeFirstRep());
    }

    default void createServiceRequestDataElement(QdmDataElement qdmDataElement) {

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setReason(createReason());
    }

    default void checkNegation(QdmToFhirConversionResult<ServiceRequest> result, ServiceRequest.ServiceRequestIntent intent) {
        checkDataElement(result);

        assertEquals(ServiceRequest.ServiceRequestStatus.COMPLETED, result.getFhirResource().getStatus());
        assertEquals(intent, result.getFhirResource().getIntent());
        assertTrue(result.getFhirResource().getDoNotPerform());

        assertEquals(1, result.getFhirResource().getExtension().size());
        assertEquals(QICORE_DO_NOT_PERFORM_REASON, result.getFhirResource().getExtension().get(0).getUrl());
        checkNegationRationaleType(result.getFhirResource().getExtension().get(0).getValue());
    }

    default void checkWithoutNegationResult(QdmToFhirConversionResult<ServiceRequest> result, ServiceRequest.ServiceRequestIntent intent) {
        checkDataElement(result);
        assertEquals(ServiceRequest.ServiceRequestStatus.UNKNOWN, result.getFhirResource().getStatus());
        assertEquals(intent, result.getFhirResource().getIntent());
        assertFalse(result.getFhirResource().getDoNotPerform());
    }

}
