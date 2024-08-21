package gov.cms.mat.patients.conversion.controller;

import gov.cms.madie.models.measure.TestCase;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionResult;
import gov.cms.mat.patients.conversion.service.PatientConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    TestCase testCase;
    ConversionResult conversionResult;
    @Mock
    private PatientConversionService patientConversionService;
    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void beforeEach() {
        testCase = TestCase.builder().build();
        conversionResult = ConversionResult.builder().build();
    }

    @Test
    void convertOne() {
        when(patientConversionService.processOne(testCase)).thenReturn(conversionResult);

        assertEquals(conversionResult, patientController.convertOne(testCase));
    }

    @Test
    void convertMany() {
        var testCaseList = List.of(testCase);
        when(patientConversionService.processMany(testCaseList)).thenReturn(List.of(conversionResult));
        List<ConversionResult> results = patientController.convertMany(testCaseList);

        assertEquals(1, results.size());
        assertEquals(conversionResult, results.get(0));
    }
}
