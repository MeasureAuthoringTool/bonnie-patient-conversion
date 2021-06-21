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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ProcedurePerformedConverterTest extends BaseConversionTest implements FhirConversionTest, ProcedureConversionTest {
    @Autowired
    private ProcedurePerformedConverter procedurePerformedConverter;

    @Test
    void getQdmType() {
        assertEquals(ProcedurePerformedConverter.QDM_TYPE, procedurePerformedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        createNoNegation(qdmDataElement);

        QdmToFhirConversionResult<Procedure> result = procedurePerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkNoNegation(result);

        assertEquals(2, result.getFhirResource().getExtension().size());
        checkRecordedExtension(result.getFhirResource().getExtension().get(1));
    }

    @Test
    void convertToFhirNegation() {
        createNegation(qdmDataElement);

        QdmToFhirConversionResult<Procedure> result = procedurePerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkNegation(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Procedure> result = procedurePerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Procedure.ProcedureStatus.COMPLETED, result.getFhirResource().getStatus());
    }
}