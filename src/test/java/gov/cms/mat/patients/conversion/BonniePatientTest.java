package gov.cms.mat.patients.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BonniePatientTest implements ResourceFileUtil {
    @SneakyThrows
    @Test
    void testBonniePatient() {
        String json = getStringFromResource("/bonnie_sample.json");

        ObjectMapper objectMapper = new ObjectMapper();

        BonniePatient bonniePatient = objectMapper.readValue(json, BonniePatient.class);

        assertNotNull(bonniePatient);
    }
}
