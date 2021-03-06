package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.exceptions.PatientConversionException;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface DataElementFinder {
    default CodeableConcept convertToCodeableConcept(CodeSystemEntriesService codeSystemEntriesService,
                                                     @Nonnull List<QdmCodeSystem> dataElementCodes) {
        var codeableConcept = new CodeableConcept();

        List<Coding> codings = dataElementCodes.stream()
                .map(c -> convertToCoding(codeSystemEntriesService, c))
                .collect(Collectors.toList());

        return codeableConcept.setCoding(codings);
    }

    default CodeableConcept convertToCodeableConcept(CodeSystemEntriesService codeSystemEntriesService, QdmCodeSystem qdmCodeSystem) {
        var codeableConcept = new CodeableConcept();
        codeableConcept.getCoding().add(convertToCoding(codeSystemEntriesService, qdmCodeSystem));
        return codeableConcept;
    }

    default Coding convertToCoding(CodeSystemEntriesService codeSystemEntriesService, QdmCodeSystem qdmCodeSystem) {
        String theSystem;

        try {
            var codeSystemEntry = codeSystemEntriesService.findRequired(qdmCodeSystem.getSystem());
            theSystem = codeSystemEntry.getUrl();
        } catch (PatientConversionException e) {
            theSystem = "urn:oid:" + qdmCodeSystem.getSystem();
        }

        return new Coding(theSystem, qdmCodeSystem.getCode(), qdmCodeSystem.getDisplay());
    }

    default Coding convertToCoding(CodeSystemEntriesService codeSystemEntriesService,
                                   List<QdmCodeSystem> dataElementCodes) {
        if (CollectionUtils.isEmpty(dataElementCodes)) {
            return null;
        } else {
            var qdmCodeSystem = dataElementCodes.get(0);// if many will take 1st one

            var optional = codeSystemEntriesService.find(qdmCodeSystem.getSystem());

            if (optional.isEmpty()) {
                return new Coding(qdmCodeSystem.getSystem(), qdmCodeSystem.getCode(), qdmCodeSystem.getDisplay());
            } else {
                var codeSystemEntry = optional.get();
                return new Coding(codeSystemEntry.getUrl(), qdmCodeSystem.getCode(), qdmCodeSystem.getDisplay());
            }
        }
    }

    default Optional<QdmDataElement> findOptionalDataElementsByType(BonniePatient bonniePatient, String type) {
        List<QdmDataElement> dataElements = findDataElementsByType(bonniePatient, type);

        if (dataElements.isEmpty()) {
            return Optional.empty();
        } else {
            if (dataElements.size() > 1) {
                throw new PatientConversionException(String.format("Patient %s has %s %s elements. Should be one.",
                        bonniePatient.identifier(), dataElements.size(), type));
            } else {
                return Optional.of(dataElements.get(0));
            }
        }
    }

    default QdmDataElement findOneDataElementsByType(BonniePatient bonniePatient, String type) {
        List<QdmDataElement> dataElements = findDataElementsByType(bonniePatient, type);

        if (dataElements.isEmpty()) {
            throw new PatientConversionException(String.format("Patient %s has no %s",
                    bonniePatient.identifier(), type));
        } else {
            if (dataElements.size() > 1) {
                throw new PatientConversionException(String.format("Patient %s has %s %s elements. Should be one.",
                        bonniePatient.identifier(), dataElements.size(), type));
            } else {
                return dataElements.get(0);
            }
        }
    }

    default List<QdmDataElement> findDataElementsByType(BonniePatient bonniePatient, String type) {
        var qdmPatient = bonniePatient.getQdmPatient();

        if (CollectionUtils.isEmpty(qdmPatient.getDataElements())) {
            return Collections.emptyList();
        } else {
            return qdmPatient.getDataElements().stream()
                    .filter(d -> d.getQdmType().equals(type))
                    .collect(Collectors.toList());
        }
    }

    default QdmCodeSystem findOneCodeSystemWithRequiredDisplay(BonniePatient bonniePatient, String type) {
        var qdmDataElement = findOneDataElementsByType(bonniePatient, type);

        var qdmCodeSystem = getOneCodeSystem(qdmDataElement);

        if (qdmCodeSystem.getDisplay() == null) {
            throw new PatientConversionException(String.format("DataElement %s has no dataElementCodes. codeSystem: %s",
                    qdmDataElement.identifier(), qdmCodeSystem));
        }

        return qdmCodeSystem;
    }

    default QdmCodeSystem getOneCodeSystem(QdmDataElement element) {
        List<QdmCodeSystem> dataElementCodes = element.getDataElementCodes();

        if (dataElementCodes.isEmpty()) {
            throw new PatientConversionException(String.format("DataElement %s has no dataElementCodes",
                    element.identifier()));
        } else {
            if (dataElementCodes.size() > 1) {
                throw new PatientConversionException(String.format("DataElement %s has %s dataElementCodes. Should be one.",
                        element.identifier(), dataElementCodes.size()));
            } else {
                return dataElementCodes.get(0);
            }
        }
    }
}
