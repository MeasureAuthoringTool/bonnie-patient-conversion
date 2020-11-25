package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.validation.ValidationResult;
import gov.cms.mat.patients.conversion.conversion.helpers.DataElementFinder;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirPatientResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.results.ConversionOutcome;
import gov.cms.mat.patients.conversion.exceptions.PatientConversionException;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PatientConverter implements DataElementFinder, FhirCreator {
    public static final String US_CORE_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
    public static final String DETAILED_RACE_URL = "http://hl7.org/fhir/us/core/ValueSet/detailed-race";
    private static final String[] NOT_MAPPED_QDM_TYPES = {"QDM::DeviceOrder", "QDM::SubstanceOrder", "QDM::SubstanceRecommended"};

    private static final String NOT_MAPPED_MESSAGE = "Did not convert %d dataElements with type %s";

    private final ValidationService validationService;

    public PatientConverter(ValidationService validationService) {
        this.validationService = validationService;
    }

    public QdmToFhirPatientResult convert(BonniePatient bonniePatient, Set<String> qdmTypes) {
        if (bonniePatient == null) {
            return buildError("Bonnie patient is null");
        } else if (bonniePatient.getQdmPatient() == null) {
            return buildError("QdmPatient is null");
        } else if (CollectionUtils.isEmpty(bonniePatient.getQdmPatient().getDataElements())) {
            return buildError("QdmPatient's data elements array is empty");
        } else {
            return getQdmToFhirPatientResult(bonniePatient, qdmTypes);
        }
    }

    public QdmToFhirPatientResult getQdmToFhirPatientResult(BonniePatient bonniePatient, Set<String> qdmTypes) {
        List<String> conversionMessages = createNotMappedMessages(bonniePatient.getQdmPatient().getDataElements(), qdmTypes);
        Patient patient = process(bonniePatient);

        ValidationResult validationResult = validationService.validate(patient);

        ConversionOutcome outcome = ConversionOutcome.builder()
                .conversionMessages(conversionMessages)
                .validationMessages(validationResult.getMessages())
                .build();

        return QdmToFhirPatientResult.builder()
                .outcome(outcome)
                .fhirPatient(patient)
                .build();
    }

    public QdmToFhirPatientResult buildError(String message) {
        ConversionOutcome outcome = ConversionOutcome.builder()
                .conversionMessages(List.of(message))
                .build();

        return QdmToFhirPatientResult.builder()
                .outcome(outcome)
                .build();
    }

    private List<String> createNotMappedMessages(List<QdmDataElement> dataElements, Set<String> qdmTypes) {
        return Arrays.stream(NOT_MAPPED_QDM_TYPES)
                .filter(qdmTypes::contains)
                .map(type -> createNotMappedMessage(type, dataElements))
                .collect(Collectors.toList());
    }

    private String createNotMappedMessage(String type, List<QdmDataElement> dataElements) {
        long count = dataElements.stream()
                .filter(d -> d.getQdmType().equals(type))
                .count();

        return String.format(NOT_MAPPED_MESSAGE, count, type);
    }

    private Patient process(BonniePatient bonniePatient) {
        Patient fhirPatient = new Patient();
        fhirPatient.setId(bonniePatient.getId());

        fhirPatient.getExtension().add(new Extension(US_CORE_RACE_URL));
        fhirPatient.getExtension().add(new Extension(DETAILED_RACE_URL));

        fhirPatient.setActive(true);

        fhirPatient.getName().add(createName(bonniePatient));

        fhirPatient.setBirthDate(bonniePatient.getQdmPatient().getBirthDatetime());

        fhirPatient.setGender(processSex(bonniePatient));
        processRace(bonniePatient, fhirPatient);

        processEthnicity(bonniePatient, fhirPatient);
        fhirPatient.setDeceased(processExpired(bonniePatient));

        return fhirPatient;
    }

    private DateTimeType processExpired(BonniePatient bonniePatient) {
        var optional = findOptionalDataElementsByType(bonniePatient, "QDM::PatientCharacteristicExpired");

        if (optional.isPresent()) {
            QdmDataElement dataElement = optional.get();
            log.trace("Patient is dead");
            return new DateTimeType(dataElement.getExpiredDatetime());
        } else {
            log.trace("Patient is alive");
            return null;
        }
    }

    private HumanName createName(BonniePatient bonniePatient) {
        HumanName humanName = new HumanName();
        humanName.setUse(HumanName.NameUse.USUAL); // ??

        humanName.setFamily(bonniePatient.getFamilyName());

        if (!CollectionUtils.isEmpty(bonniePatient.getGivenNames())) {
            List<StringType> fhirNames = bonniePatient.getGivenNames().stream()
                    .map(StringType::new)
                    .collect(Collectors.toList());

            humanName.setGiven(fhirNames);
        }

        return humanName;
    }

    private void processRace(BonniePatient bonniePatient, Patient fhirPatient) {
        try {
            QdmCodeSystem qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(bonniePatient, "QDM::PatientCharacteristicRace");
            Extension extension = fhirPatient.getExtensionByUrl(US_CORE_RACE_URL);

            extension.setValue(new CodeType(qdmCodeSystem.getCode()));
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
        }
    }

    private void processEthnicity(BonniePatient bonniePatient, Patient fhirPatient) {
        try {
            QdmCodeSystem qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(bonniePatient, "QDM::PatientCharacteristicEthnicity");
            Extension extension = fhirPatient.getExtensionByUrl(DETAILED_RACE_URL);

            extension.setValue(new CodeType(qdmCodeSystem.getCode()));
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
        }
    }

    private Enumerations.AdministrativeGender processSex(BonniePatient bonniePatient) {
        try {
            QdmCodeSystem qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(bonniePatient, "QDM::PatientCharacteristicSex");

            Enumerations.AdministrativeGender value = Enumerations.AdministrativeGender.UNKNOWN;
            if (qdmCodeSystem.getDisplay().toLowerCase().startsWith("m")) { // sometimes Male and M
                value = Enumerations.AdministrativeGender.MALE;
            }

            if (qdmCodeSystem.getDisplay().toLowerCase().startsWith("f")) {
                value = Enumerations.AdministrativeGender.FEMALE;
            }

            return value;
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
