package gov.cms.mat.patients.conversion;

import gov.cms.mat.patients.conversion.service.PatientConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PatientsApplicationTests {
    @Autowired
    private PatientConversionService patientConversionService;

    @Test
    void contextLoads() {
        assertNotNull(patientConversionService);
    }
}
