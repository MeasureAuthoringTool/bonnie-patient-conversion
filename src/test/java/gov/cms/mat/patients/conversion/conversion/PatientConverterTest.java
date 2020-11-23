package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.conversion.helpers.DataElementFinder;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirPatientResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPatient;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Set;

import static gov.cms.mat.patients.conversion.conversion.PatientConverter.DETAILED_RACE_URL;
import static gov.cms.mat.patients.conversion.conversion.PatientConverter.US_CORE_RACE_URL;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PatientConverterTest implements ResourceFileUtil, FhirCreator, DataElementFinder {

    Set<String> TYPES = Set.of("QDM::DeviceOrder", "QDM::SubstanceOrder", "QDM::SubstanceRecommended", "QDM::MedicationAdministered", "QDM::ImmunizationOrder");
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private PatientConverter patientConverter;
    private BonniePatient bonniePatient;
    private int count;

    @SneakyThrows
    @BeforeEach
    void before() {
        String fromResource = getStringFromResource("/patient_one.json");
        bonniePatient = objectMapper.readValue(fromResource, BonniePatient.class);
        count = 0;
    }

    @Test
    void convertMin() {
        Set<String> qdmTypes = collectQdmTypes(bonniePatient);
        bonniePatient = null;

        QdmToFhirPatientResult result = patientConverter.convert(bonniePatient, qdmTypes);
        assertEquals(1, result.getOutcome().getConversionMessages().size());
        assertEquals("Bonnie patient is null", result.getOutcome().getConversionMessages().get(0));

        bonniePatient = new BonniePatient();
        result = patientConverter.convert(bonniePatient, qdmTypes);
        assertEquals(1, result.getOutcome().getConversionMessages().size());
        assertEquals("QdmPatient is null", result.getOutcome().getConversionMessages().get(0));

        bonniePatient.setQdmPatient(new QdmPatient());

    }


    @Test
    void convert() {
        Set<String> qdmTypes = collectQdmTypes(bonniePatient);

        QdmToFhirPatientResult result = patientConverter.convert(bonniePatient, qdmTypes);

        assertEquals(0, result.getOutcome().getConversionMessages().size());
        assertEquals(0, result.getOutcome().getValidationMessages().size());

        assertEquals(bonniePatient.getId(), result.getFhirPatient().getId());
        assertTrue(result.getFhirPatient().getActive());

        assertEquals(1, result.getFhirPatient().getName().size());
        assertEquals("Patient", result.getFhirPatient().getName().get(0).getFamily());
        assertEquals(1, result.getFhirPatient().getName().get(0).getGiven().size());
        assertEquals("Test", result.getFhirPatient().getName().get(0).getGiven().get(0).getValue());

        assertEquals(bonniePatient.getQdmPatient().getBirthDatetime(), result.getFhirPatient().getBirthDate());

        assertEquals(Enumerations.AdministrativeGender.MALE, result.getFhirPatient().getGender());

        Extension raceExtension = result.getFhirPatient().getExtensionByUrl(US_CORE_RACE_URL);
        assertThat(raceExtension.getValue(), instanceOf(CodeType.class));
        CodeType raceCodeType = (CodeType) raceExtension.getValue();
        assertEquals("1002-5", raceCodeType.getCode());

        Extension ethnicityExtension = result.getFhirPatient().getExtensionByUrl(DETAILED_RACE_URL);
        assertThat(ethnicityExtension.getValue(), instanceOf(CodeType.class));
        CodeType ethnicityCodeType = (CodeType) ethnicityExtension.getValue();
        assertEquals("2186-5", ethnicityCodeType.getCode());

        assertFalse(result.getFhirPatient().hasDeceased()); // Im not dead yet
    }

    @Test
    void convertExpired() {
        Date deathDate = new Date();

        var expiredElement = create("QDM::PatientCharacteristicExpired");
        expiredElement.setExpiredDatetime(deathDate);
        bonniePatient.getQdmPatient().getDataElements().add(expiredElement);

        QdmToFhirPatientResult result = patientConverter.convert(bonniePatient, collectQdmTypes(bonniePatient));

        assertEquals(0, result.getOutcome().getConversionMessages().size());
        assertEquals(0, result.getOutcome().getValidationMessages().size());

        assertTrue(result.getFhirPatient().hasDeceased()); // Im dead
        assertEquals(deathDate, result.getFhirPatient().getDeceasedDateTimeType().getValue());
    }

    @Test
    void convertFemale() {
        QdmCodeSystem qdmCodeSystem = findOneCodeSystemWithRequiredDisplay(bonniePatient, "QDM::PatientCharacteristicSex");
        qdmCodeSystem.setDisplay("f");

        QdmToFhirPatientResult result = patientConverter.convert(bonniePatient, collectQdmTypes(bonniePatient));

        assertEquals(0, result.getOutcome().getConversionMessages().size());
        assertEquals(0, result.getOutcome().getValidationMessages().size());

        assertEquals(Enumerations.AdministrativeGender.FEMALE, result.getFhirPatient().getGender());
    }

    @Test
    void convertNotMapped() {
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::DeviceOrder"));
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::SubstanceOrder"));
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::SubstanceOrder"));
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::SubstanceRecommended"));
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::SubstanceRecommended"));
        bonniePatient.getQdmPatient().getDataElements().add(create("QDM::SubstanceRecommended"));

        QdmToFhirPatientResult result = patientConverter.convert(bonniePatient, collectQdmTypes(bonniePatient));

        assertEquals(3, result.getOutcome().getConversionMessages().size());
        assertEquals(0, result.getOutcome().getValidationMessages().size());

        assertEquals("Did not convert 1 dataElements with type QDM::DeviceOrder",
                result.getOutcome().getConversionMessages().get(0));

        assertEquals("Did not convert 2 dataElements with type QDM::SubstanceOrder",
                result.getOutcome().getConversionMessages().get(1));

        assertEquals("Did not convert 3 dataElements with type QDM::SubstanceRecommended",
                result.getOutcome().getConversionMessages().get(2));
    }

    private QdmDataElement create(String type) {
        QdmDataElement qdmDataElement = new QdmDataElement();
        qdmDataElement.setId("" + count++);
        qdmDataElement.setQdmType(type);
        return qdmDataElement;
    }
}