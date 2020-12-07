package gov.cms.mat.patients.conversion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PatientConversionServiceTest implements ResourceFileUtil {
    @Autowired
    private PatientConversionService patientConversionService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void processOne() {
        String fromResource = getStringFromResource("/patient_one.json");
        BonniePatient bonniePatient = objectMapper.readValue(fromResource, BonniePatient.class);
        ConversionResult conversionResult = patientConversionService.processOne(bonniePatient);
        assertNotNull(conversionResult);

        assertEquals(bonniePatient.getId(), conversionResult.getId());
        assertEquals(bonniePatient.getId(), conversionResult.getFhirPatient().get("id").asText());
        assertEquals(bonniePatient.getMeasureIds(), conversionResult.getMeasureIds());
    }

    @SneakyThrows
    @Test
    void processHundred() {
        String fromResource = getStringFromResource("/patients_100.json");
        BonniePatient[] patients = objectMapper.readValue(fromResource, BonniePatient[].class);

        List<ConversionResult> conversionResults = patientConversionService.processMany(Arrays.asList(patients));
        assertEquals(patients.length, conversionResults.size());
    }

    @SneakyThrows
    @Test
    void processTypes() {
        String fromResource = getStringFromResource("/patients_types.json");
        BonniePatient[] patients = objectMapper.readValue(fromResource, BonniePatient[].class);

        List<ConversionResult> conversionResults = patientConversionService.processMany(Arrays.asList(patients));
        assertEquals(patients.length, conversionResults.size());
    }
}