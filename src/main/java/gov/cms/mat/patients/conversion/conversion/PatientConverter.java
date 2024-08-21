package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import gov.cms.madie.models.measure.TestCase;
import gov.cms.mat.patients.conversion.conversion.helpers.DataElementFinder;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirPatientResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionOutcome;
import gov.cms.mat.patients.conversion.exceptions.PatientConversionException;
import gov.cms.mat.patients.conversion.service.ValidationService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
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

    public static final String DETAILED_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";

    public static final String BIRTH_TIME_URL = "http://hl7.org/fhir/StructureDefinition/patient-birthTime";
    private static final String[] NOT_MAPPED_QDM_TYPES = {"QDM::DeviceOrder", "QDM::SubstanceOrder", "QDM::SubstanceRecommended"};

    private static final String NOT_MAPPED_MESSAGE = "Did not convert %d dataElements with type %s";

    private final ValidationService validationService;

    public PatientConverter(ValidationService validationService) {
        this.validationService = validationService;
    }

    public QdmToFhirPatientResult convert(TestCase testCase, QdmPatient qdmPatient, Set<String> qdmTypes) {
        if (testCase == null) {
            return buildError("Bonnie patient is null");
        } else if (qdmPatient == null) {
            return buildError("QdmPatient is null");
        } else if (CollectionUtils.isEmpty(qdmPatient.getDataElements())) {
            return buildError("QdmPatient's data elements array is empty");
        } else {
            return getQdmToFhirPatientResult(testCase, qdmPatient, qdmTypes);
        }
    }

    public QdmToFhirPatientResult getQdmToFhirPatientResult(TestCase testCase, QdmPatient qdmPatient, Set<String> qdmTypes) {
        List<String> conversionMessages = createNotMappedMessages(qdmPatient.getDataElements(), qdmTypes);
        var patient = process(testCase, qdmPatient);

        var validationResult = validationService.validate(patient);

        var conversionOutcome = ConversionOutcome.builder()
                .conversionMessages(conversionMessages)
                .validationMessages(validationResult.getMessages())
                .build();

        return QdmToFhirPatientResult.builder()
                .outcome(conversionOutcome)
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

    private Patient process(TestCase testCase, QdmPatient qdmPatient) {
        var fhirPatient = new Patient();
        fhirPatient.setId(testCase.getPatientId().toString());
        fhirPatient.setMeta(new Meta()
                .addProfile("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-patient"));

        processRace(testCase, qdmPatient, fhirPatient);
        processEthnicity(testCase, qdmPatient, fhirPatient);

        // Identifier
        fhirPatient.setActive(true);
        fhirPatient.getName().add(createName(testCase));
        fhirPatient.setGender(processSex(testCase, qdmPatient));
        fhirPatient.setDeceased(processExpired(testCase, qdmPatient));
        fhirPatient.setBirthDate(qdmPatient.getBirthDatetime());

        if (qdmPatient.getExtendedData() != null &&
                StringUtils.isNotBlank(qdmPatient.getExtendedData().getMedicalRecordNumber())) {
            String medicalRecordNumber = qdmPatient.getExtendedData().getMedicalRecordNumber();

            fhirPatient.getIdentifierFirstRep().setType(createCodeableConcept("http://terminology.hl7.org/CodeSystem/v2-0203", "MR", null));
            fhirPatient.getIdentifierFirstRep().setValue(medicalRecordNumber);
        }
        return fhirPatient;
    }

    private DateTimeType processExpired(TestCase testCase, QdmPatient qdmPatient) {
        var optional = findOptionalDataElementsByType(testCase, qdmPatient, "QDM::PatientCharacteristicExpired");

        if (optional.isPresent()) {
            QdmDataElement dataElement = optional.get();
            log.trace("Patient is dead");
            if (dataElement.getExpiredDatetime() != null)
                dataElement.getExpiredDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            return dataElement.getExpiredDatetime();
        } else {
            log.trace("Patient is alive");
            return null;
        }
    }

    private HumanName createName(TestCase testCase) {
        var humanName = new HumanName();
        humanName.setUse(HumanName.NameUse.USUAL); // ??
        humanName.setFamily(testCase.getSeries());
        humanName.setGiven(List.of(new StringType(testCase.getTitle())));
        return humanName;
    }

    private void processRace(TestCase testCase, QdmPatient qdmPatient, Patient fhirPatient) {
        try {
            var qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(testCase, qdmPatient,"QDM::PatientCharacteristicRace");
            var extension = new Extension(US_CORE_RACE_URL);
            var subExtension = new Extension("ombCategory");
            subExtension.setValue(createCodeableConcept(qdmCodeSystem.getSystem(), qdmCodeSystem.getCode(), qdmCodeSystem.getDisplay()));
            extension.setExtension(List.of(subExtension));
            fhirPatient.getExtension().add(extension);
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
        }
    }

    private void processEthnicity(TestCase testCase, QdmPatient qdmPatient, Patient fhirPatient) {
        try {
            var qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(testCase, qdmPatient,"QDM::PatientCharacteristicEthnicity");
            var extension = new Extension(DETAILED_RACE_URL);
            var subExtension = new Extension("ombCategory");
            subExtension.setValue(createCodeableConcept(qdmCodeSystem.getSystem(), qdmCodeSystem.getCode(), qdmCodeSystem.getDisplay()));
            extension.setExtension(List.of(subExtension));
            fhirPatient.getExtension().add(extension);
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
        }
    }

    private Enumerations.AdministrativeGender processSex(TestCase testCase, QdmPatient qdmPatient) {
        try {
            var qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(testCase, qdmPatient, "QDM::PatientCharacteristicSex");

            var administrativeGender = Enumerations.AdministrativeGender.UNKNOWN;
            if (qdmCodeSystem.getDisplay().toLowerCase().startsWith("m")) { // sometimes Male and M
                administrativeGender = Enumerations.AdministrativeGender.MALE;
            }

            if (qdmCodeSystem.getDisplay().toLowerCase().startsWith("f")) {
                administrativeGender = Enumerations.AdministrativeGender.FEMALE;
            }

            return administrativeGender;
        } catch (PatientConversionException e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
