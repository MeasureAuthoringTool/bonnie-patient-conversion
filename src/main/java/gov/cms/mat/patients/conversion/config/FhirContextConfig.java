package gov.cms.mat.patients.conversion.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FhirContextConfig {
    @Bean
    public FhirContext fhirContext() {
        FhirContext fhirContext = FhirContext.forR4();
        fhirContext.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
        log.info("Created: {} ", fhirContext);
        return fhirContext;
    }
}
