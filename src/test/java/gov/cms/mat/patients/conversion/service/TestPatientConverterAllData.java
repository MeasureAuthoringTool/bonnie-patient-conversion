package gov.cms.mat.patients.conversion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled //takes to long to run as unit test
@SpringBootTest
@ActiveProfiles("test")
class TestPatientConverterAllData implements ResourceFileUtil {
    @Autowired
    PatientConversionService patientConversionService;

    @SneakyThrows
    @Test
    void process() {
        String all = getStringFromResource("/cqm_patients.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> splits = Stream.of(all.split("\n"))
                .map(String::new)
                .collect(Collectors.toList());

        for (String split : splits) {
            try {
                BonniePatient      bonniePatient = objectMapper.readValue(split, BonniePatient.class);
                System.out.println(bonniePatient.getId());
                ConversionResult conversionResult = patientConversionService.processOne(bonniePatient);
                assertNotNull(conversionResult);

            } catch (Exception e) {
                System.out.println(split);
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    @Test
    void load() {
        String all = getStringFromResource("/patients_all_QDM_PROD_as_array.json");
        ObjectMapper objectMapper = new ObjectMapper();
        BonniePatient[] patients = objectMapper.readValue(all, BonniePatient[].class);

        List<ConversionResult>  results =   patientConversionService.processMany(Arrays.asList(patients));
        assertEquals( patients.length, results.size());
    }
}