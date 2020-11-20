package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirPatientResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PatientConverterTest implements ResourceFileUtil, FhirCreator {

    Set<String> TYPES = Set.of("QDM::DeviceOrder", "QDM::SubstanceOrder", "QDM::SubstanceRecommended", "QDM::MedicationAdministered", "QDM::ImmunizationOrder");
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private PatientConverter patientConverter;

    @SneakyThrows
    @Test
    void convert() {
        String fromResource = getStringFromResource("/patient_one.json");
        BonniePatient bonniePatient = objectMapper.readValue(fromResource, BonniePatient.class);

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

    }
}