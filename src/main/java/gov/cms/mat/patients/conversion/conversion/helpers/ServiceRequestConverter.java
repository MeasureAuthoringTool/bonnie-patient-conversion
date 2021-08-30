package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

public interface ServiceRequestConverter extends DataElementFinder, FhirCreator {

    default QdmToFhirConversionResult<ServiceRequest>
    convertToFhirServiceRequest(Patient fhirPatient,
                                QdmDataElement qdmDataElement,
                                ConverterBase<ServiceRequest> converterBase,
                                ServiceRequest.ServiceRequestIntent intent) {
        List<String> conversionMessages = new ArrayList<>();

        var serviceRequest = new ServiceRequest();
        serviceRequest.setSubject(createPatientReference(fhirPatient));

        serviceRequest.setIntent(intent);

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            serviceRequest.setCode(converterBase.convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        serviceRequest.setId(qdmDataElement.getId());

        if (qdmDataElement.getReason() != null) {
            serviceRequest.getReasonCode().add(converterBase.convertToCodeableConcept(qdmDataElement.getReason()));
        }

        serviceRequest.setAuthoredOnElement(qdmDataElement.getAuthorDatetime());

        if (!converterBase.processNegation(qdmDataElement, serviceRequest)) {
            serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
        }

        return QdmToFhirConversionResult.<ServiceRequest>builder()
                .fhirResource(serviceRequest)
                .conversionMessages(conversionMessages)
                .build();
    }
}
