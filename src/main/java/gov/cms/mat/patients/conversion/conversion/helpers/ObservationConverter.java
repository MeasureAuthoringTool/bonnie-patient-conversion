package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmComponent;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ObservationConverter extends FhirCreator, DataElementFinder {

    default QdmToFhirConversionResult<Observation> convertToFhirObservation(Patient fhirPatient,
                                                                            QdmDataElement qdmDataElement,
                                                                            ConverterBase<Observation> converterBase) {
        List<String> conversionMessages = new ArrayList<>();
        var observation = new Observation();
        observation.setId(qdmDataElement.getId());
        observation.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            observation.setCode(convertToCodeableConcept(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getResult() != null) {
            var resultProcessor =
                    new JsonNodeObservationResultProcessor(converterBase.getCodeSystemEntriesService(), conversionMessages);

            observation.setValue(resultProcessor.findType(qdmDataElement.getResult()));
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            observation.setEffective(convertPeriod(qdmDataElement.getRelevantPeriod()));
        }

        if (!observation.hasEffectivePeriod() && qdmDataElement.getRelevantDatetime() != null) {
            observation.setEffective(qdmDataElement.getRelevantDatetime());
        }

        if (!converterBase.processNegation(qdmDataElement, observation)) {
            observation.setStatus(Observation.ObservationStatus.FINAL);
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            observation.setIssuedElement(new InstantType(qdmDataElement.getAuthorDatetime()));
        } else if (qdmDataElement.getResultDatetime() != null) {
            observation.setIssuedElement(new InstantType(qdmDataElement.getResultDatetime()));
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getComponents())) {
            List<Observation.ObservationComponentComponent> fhirComponents = processComponents(qdmDataElement.getComponents(),
                    converterBase.getCodeSystemEntriesService(), conversionMessages);

            observation.setComponent(fhirComponents);
        }

        if (qdmDataElement.getReason() != null) {
            conversionMessages.add("Cannot convert reason to basedOn. The Observation.basedOn concept indicates the plan, proposal " +
                    "or order that the observation fulfills. This concept is not consistent with the QDM concept of " +
                    "“reason” for the study to be performed.");
        }

        processReferenceRange(qdmDataElement, observation);

        if (qdmDataElement.getMethod() != null) {
            observation.setMethod(convertToCodeableConcept(converterBase.getCodeSystemEntriesService(), qdmDataElement.getMethod()));
        }

        return QdmToFhirConversionResult.<Observation>builder()
                .fhirResource(observation)
                .conversionMessages(conversionMessages)
                .build();
    }

    private void processReferenceRange(QdmDataElement qdmDataElement, Observation observation) {
        if (qdmDataElement.getReferenceRange() != null) {
            if (qdmDataElement.getReferenceRange().getHigh() != null) {
                observation.getReferenceRangeFirstRep()
                        .setHigh(convertQuantity(qdmDataElement.getReferenceRange().getHigh()));
            }

            if (qdmDataElement.getReferenceRange().getLow() != null) {
                observation.getReferenceRangeFirstRep()
                        .setLow(convertQuantity(qdmDataElement.getReferenceRange().getLow()));
            }
        }
    }

    private List<Observation.ObservationComponentComponent> processComponents(List<QdmComponent> qdmComponents,
                                                                              CodeSystemEntriesService codeSystemEntriesService,
                                                                              List<String> conversionMessages) {
        return qdmComponents.stream()
                .map(c -> convertComponent(c, codeSystemEntriesService, conversionMessages))
                .collect(Collectors.toList());
    }

    default Observation.ObservationComponentComponent convertComponent(QdmComponent qdmComponent,
                                                                       CodeSystemEntriesService codeSystemEntriesService,
                                                                       List<String> conversionMessages) {
        var component = new Observation.ObservationComponentComponent();

        if (qdmComponent.getCode() != null) {
            var qdmCodeSystem = new QdmCodeSystem();
            qdmCodeSystem.setDisplay(qdmComponent.getCode().getDisplay());
            qdmCodeSystem.setCode(qdmComponent.getCode().getCode());
            qdmCodeSystem.setSystem(qdmComponent.getCode().getSystem());

            component.setCode(convertToCodeableConcept(codeSystemEntriesService, qdmCodeSystem));
        }

        if (qdmComponent.getResult() != null) {
            var resultProcessor =
                    new JsonNodeObservationResultProcessor(codeSystemEntriesService, conversionMessages);

            component.setValue(resultProcessor.findType(qdmComponent.getResult()));
        }

        return component;
    }
}
