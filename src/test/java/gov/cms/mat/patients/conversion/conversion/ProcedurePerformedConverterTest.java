package gov.cms.mat.patients.conversion.conversion;

import com.fasterxml.jackson.databind.node.BooleanNode;
import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.ProcedureConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Procedure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        checkNoNegation(result);
    }

    @Test
    void convertToFhirNegation() {
        createNegation(qdmDataElement);

        QdmToFhirConversionResult<Procedure> result = procedurePerformedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkNegation(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Procedure> result = procedurePerformedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Procedure.ProcedureStatus.COMPLETED, result.getFhirResource().getStatus());
    }
}