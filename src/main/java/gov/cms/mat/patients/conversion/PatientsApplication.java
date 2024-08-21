package gov.cms.mat.patients.conversion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@Slf4j
public class PatientsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientsApplication.class, args);
    }

    /**
     * Force UTC timezone locally.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Set timezone to UTC.");
    }
}
