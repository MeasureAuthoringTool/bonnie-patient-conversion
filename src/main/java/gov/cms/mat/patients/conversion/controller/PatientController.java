package gov.cms.mat.patients.conversion.controller;

import gov.cms.madie.models.measure.TestCase;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionResult;
import gov.cms.mat.patients.conversion.service.PatientConversionService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientConversionService patientConversionService;

    public PatientController(PatientConversionService patientConversionService) {
        this.patientConversionService = patientConversionService;
    }

    @PutMapping("/convertOne")
    public ConversionResult convertOne(@RequestBody TestCase testCase) {
        return patientConversionService.processOne(testCase);
    }

    @PutMapping("/convertMany")
    public List<ConversionResult> convertMany(@RequestBody List<TestCase> testCases) {
        return patientConversionService.processMany(testCases);
    }
}
