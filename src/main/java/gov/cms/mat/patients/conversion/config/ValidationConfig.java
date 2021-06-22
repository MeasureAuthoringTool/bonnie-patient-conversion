package gov.cms.mat.patients.conversion.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.INCISION_DATE_TIME_URL;
import static gov.cms.mat.patients.conversion.conversion.PatientConverter.BIRTH_TIME_URL;
import static gov.cms.mat.patients.conversion.conversion.PatientConverter.DETAILED_RACE_URL;
import static gov.cms.mat.patients.conversion.conversion.PatientConverter.US_CORE_RACE_URL;

@Configuration
public class ValidationConfig {
    private final FhirContext fhirContext;

    public ValidationConfig(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
    }

    @Bean
    FhirValidator fhirValidator() {
        FhirValidator validator = fhirContext.newValidator();

        var instanceValidator = new FhirInstanceValidator(fhirContext);
        instanceValidator.setValidationSupport(fhirContext.getValidationSupport());
        instanceValidator.setNoTerminologyChecks(true);

        instanceValidator.setCustomExtensionDomains(
                US_CORE_RACE_URL,
                DETAILED_RACE_URL,
                INCISION_DATE_TIME_URL,
                BIRTH_TIME_URL
        );

        validator.registerValidatorModule(instanceValidator);

        return validator;
    }
}
