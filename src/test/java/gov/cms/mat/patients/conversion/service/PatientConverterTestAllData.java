package gov.cms.mat.patients.conversion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Disabled //takes to long to run as unit test
@SpringBootTest
@ActiveProfiles("test")
class PatientConverterTestAllData implements ResourceFileUtil {
    @Autowired
    PatientService patientService;

    @SneakyThrows
    @Test
    void process() {
        String all = getStringFromResource("/cqm_patients.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> splits = Stream.of(all.split("\n"))
                .map(String::new)
                .collect(Collectors.toList());

        Set<String> types = new TreeSet<>();
        BonniePatient bonniePatient = null;
        for (String split : splits) {
            try {
                bonniePatient = objectMapper.readValue(split, BonniePatient.class);
                System.out.println(bonniePatient.get_id());
                patientService.processOne(bonniePatient);

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

        patientService.processMany(Arrays.asList(patients));
    }
}