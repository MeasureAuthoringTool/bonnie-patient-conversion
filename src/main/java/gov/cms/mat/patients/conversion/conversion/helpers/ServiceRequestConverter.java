package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

public interface ServiceRequestConverter extends DataElementFinder, FhirCreator {

    default QdmToFhirConversionResult<ServiceRequest> convertToFhirServiceRequest(Patient fhirPatient,
                                                                                  QdmDataElement qdmDataElement,
                                                                                  ConverterBase<ServiceRequest> converterBase,
                                                                                  ServiceRequest.ServiceRequestIntent intent) {
        List<String> conversionMessages = new ArrayList<>();

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setSubject(createReference(fhirPatient));

        serviceRequest.setIntent(intent);

        serviceRequest.setCode(convertToCodeSystems(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));
        serviceRequest.setId(qdmDataElement.get_id());

        if (qdmDataElement.getReason() != null) {
            serviceRequest.setReasonCode(List.of(convertToCodeableConcept(converterBase.getCodeSystemEntriesService(),
                    qdmDataElement.getReason())));
        }

        serviceRequest.setAuthoredOn(qdmDataElement.getAuthorDatetime());

        if (!converterBase.processNegation(qdmDataElement, serviceRequest)) {
            serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.UNKNOWN);
            conversionMessages.add(ConverterBase.NO_STATUS_MAPPING);
        }

        return QdmToFhirConversionResult.<ServiceRequest>builder()
                .fhirResource(serviceRequest)
                .conversionMessages(conversionMessages)
                .build();
    }
}
