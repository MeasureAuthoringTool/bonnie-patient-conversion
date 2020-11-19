package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ProcedureConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Procedure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class InterventionPerformedConverterTest extends BaseConversionTest implements FhirConversionTest, ProcedureConversionTest {
    @Autowired
    private InterventionPerformedConverter interventionPerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(InterventionPerformedConverter.QDM_TYPE, interventionPerformedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        createNoNegation(qdmDataElement);

        QdmToFhirConversionResult<Procedure> result = interventionPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNoNegation(result);
    }

    @Test
    void convertToFhirNegation() {
        createNegation(qdmDataElement);

        QdmToFhirConversionResult<Procedure> result = interventionPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Procedure> result = interventionPerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Procedure.ProcedureStatus.COMPLETED, result.getFhirResource().getStatus());
    }
}