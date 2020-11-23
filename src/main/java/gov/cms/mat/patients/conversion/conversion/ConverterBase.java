package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.DataElementFinder;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.results.ConversionOutcome;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import gov.cms.mat.patients.conversion.exceptions.MappingServiceException;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
public abstract class ConverterBase<T extends IBaseResource> implements FhirCreator, DataElementFinder {
    public static final String SNOMED_OID = "2.16.840.1.113883.6.96";
    public static final String UCUM_SYSTEM = "http://unitsofmeasure.org";

    public static final String NO_STATUS_MAPPING = "No mapping for status";

    public static final String UNEXPECTED_DATA_LOG_MESSAGE = "Unexpected data found for qdmType: {} qdmDataElement property: {}.";

    public static final String INCISION_DATE_TIME_URL = "http://hl7.org/fhir/StructureDefinition/procedure-incisionDateTime";
    public static final String QICORE_DO_NOT_PERFORM_REASON = "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerformReason";
    public static final String QICORE_NOT_DONE = "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone";
    public static final String QICORE_NOT_DONE_REASON = "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason";
    public static final String QICORE_RECORDED = "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-recorded";

    private final FhirContext fhirContext;
    private final ObjectMapper objectMapper;
    private final ValidationService validationService;
    @Getter
    private final CodeSystemEntriesService codeSystemEntriesService;

    public ConverterBase(CodeSystemEntriesService codeSystemEntriesService,
                         FhirContext fhirContext,
                         ObjectMapper objectMapper,
                         ValidationService validationService) {
        this.codeSystemEntriesService = codeSystemEntriesService;
        this.fhirContext = fhirContext;
        this.objectMapper = objectMapper;
        this.validationService = validationService;
    }

    public Logger getLog() {
        return log;
    }

    @Async("threadPoolConversion")
    public CompletableFuture<List<FhirDataElement>> convertToFhirDataElement(BonniePatient bonniePatient, Patient fhirPatient) {
        List<FhirDataElement> encounters = createDataElements(bonniePatient, fhirPatient);
        return CompletableFuture.completedFuture(encounters);
    }

    public List<FhirDataElement> createDataElements(BonniePatient bonniePatient, Patient fhirPatient) {
        List<QdmDataElement> dataElements = findDataElementsByType(bonniePatient, getQdmType());

        if (dataElements.isEmpty()) {
            return Collections.emptyList();
        } else {
            return dataElements.stream()
                    .map(d -> convertQdmToFhir(fhirPatient, d))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    FhirDataElement buildDataElement(QdmToFhirConversionResult<T> qdmToFhirConversionResult,
                                     ValidationResult validationResult,
                                     QdmDataElement dataElement) {
        try {
            ConversionOutcome conversionOutcome = ConversionOutcome.builder()
                    .conversionMessages(qdmToFhirConversionResult.getConversionMessages())
                    .validationMessages(validationResult.getMessages())
                    .build();

            String valueSetTitle = findValueSetTitle(dataElement.getDescription()).trim();

            String description = qdmToFhirConversionResult.getFhirResource().getClass().getSimpleName() + ": " + valueSetTitle;

            return FhirDataElement.builder()
                    .codeListId(dataElement.getCodeListId())
                    .description(description)
                    .valueSetTitle(valueSetTitle)
                    .fhirResource(createJsonNodeFromFhirResource(qdmToFhirConversionResult.getFhirResource()))
                    .outcome(conversionOutcome)
                    .fhirType(qdmToFhirConversionResult.getFhirResource().fhirType())
                    .fhirId(qdmToFhirConversionResult.getFhirResource().getIdElement().getValue())
                    .fhirObject(qdmToFhirConversionResult.getFhirResource())
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Cannot create DataElement", e);
            throw new MappingServiceException(e.getMessage());
        }
    }

    private String findValueSetTitle(String description) {
        if (StringUtils.isBlank(description)) {
            return "";
        } else {
            int index = description.indexOf(":");

            if (index < 0) {
                log.warn("Cannot find valueSetTitle in description: {}", description);
                return description;
            } else {
                return description.substring(index + 1);
            }
        }
    }

    private JsonNode createJsonNodeFromFhirResource(IBaseResource fhirResource) throws JsonProcessingException {
        String json = toJson(fhirContext, fhirResource);
        return objectMapper.readTree(json);
    }

    public abstract String getQdmType();

    public abstract QdmToFhirConversionResult<T> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement);

    public FhirDataElement convertQdmToFhir(Patient fhirPatient, QdmDataElement dataElement) {
        QdmToFhirConversionResult<T> qdmToFhirConversionResult = convertToFhir(fhirPatient, dataElement);

        return validate(dataElement, qdmToFhirConversionResult);
    }

    public FhirDataElement validate(QdmDataElement dataElement, QdmToFhirConversionResult<T> qdmToFhirConversionResult) {
        if (qdmToFhirConversionResult == null) {
            return null;
        } else {
            ValidationResult validationResult = validationService.validate(qdmToFhirConversionResult.getFhirResource());

            return buildDataElement(qdmToFhirConversionResult, validationResult, dataElement);
        }
    }

    public boolean processNegation(QdmDataElement qdmDataElement, T resource) {
        if (qdmDataElement.getNegationRationale() != null) {
            convertNegation(qdmDataElement, resource);
            return true;
        } else {
            log.trace("No negations found");
            return false;
        }
    }

    void convertNegation(QdmDataElement qdmDataElement, T resource) {
        log.warn("Negation not handled for qdmDataElement id: {} - for converting {} to Fhir {}",
                qdmDataElement.getId(), qdmDataElement.getQdmType(), resource.getClass().getSimpleName());
    }

    void convertNegationServiceRequest(QdmDataElement qdmDataElement, ServiceRequest serviceRequest) {
        serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
        serviceRequest.setDoNotPerform(true);

        Extension extensionDoNotPerformReason = new Extension(QICORE_DO_NOT_PERFORM_REASON);
        extensionDoNotPerformReason.setValue(convertToCoding(codeSystemEntriesService, qdmDataElement.getNegationRationale()));
        serviceRequest.getExtension().add(extensionDoNotPerformReason);
    }

    void convertNegationObservation(QdmDataElement qdmDataElement, Observation observation) {
        observation.setStatus(Observation.ObservationStatus.FINAL);

        if (qdmDataElement.getAuthorDatetime() != null) {
            observation.getModifierExtension().add(createNotDoneExtension());
        }

        Extension extensionNotDoneReason = new Extension(QICORE_NOT_DONE_REASON);
        extensionNotDoneReason.setValue(convertToCoding(codeSystemEntriesService, qdmDataElement.getNegationRationale()));
        observation.getExtension().add(extensionNotDoneReason);
    }


    void convertNegationProcedure(QdmDataElement qdmDataElement, Procedure procedure) {
        // http://hl7.org/fhir/us/qicore/Procedure-negation-example.json.html
        procedure.setStatus(Procedure.ProcedureStatus.NOTDONE);

        procedure.getModifierExtension().add(createNotDoneExtension());

        procedure.getExtension().add(createRecordedExtension(qdmDataElement.getAuthorDatetime()));

        procedure.setStatusReason(convertToCodeableConcept(qdmDataElement.getNegationRationale()));
    }

    void convertNegationMedicationRequest(QdmDataElement qdmDataElement, MedicationRequest medicationRequest) {
        medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.COMPLETED);

        medicationRequest.setDoNotPerform(true);

        medicationRequest.getReasonCode()
                .add(convertToCodeableConcept(qdmDataElement.getNegationRationale()));
    }

    public CodeableConcept convertToCodeableConcept(QdmCodeSystem qdmCodeSystem) {
        return convertToCodeableConcept(codeSystemEntriesService, qdmCodeSystem);
    }

    public CodeableConcept convertToCodeableConcept(List<QdmCodeSystem> dataElementCodes) {
        return convertToCodeableConcept(codeSystemEntriesService, dataElementCodes);
    }

    public Coding convertToCoding(List<QdmCodeSystem> dataElementCodes) {
        return convertToCoding(codeSystemEntriesService, dataElementCodes);
    }
}
