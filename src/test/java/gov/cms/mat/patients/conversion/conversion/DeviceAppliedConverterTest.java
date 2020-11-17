package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Procedure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class DeviceAppliedConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private DeviceAppliedConverter deviceAppliedConverter;

    @Test
    void getQdmType() {
        assertEquals(DeviceAppliedConverter.QDM_TYPE, deviceAppliedConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setReason(createReason());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setPerformer(createPerformer());

        QdmToFhirConversionResult<Procedure> result = deviceAppliedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(Procedure.ProcedureStatus.INPROGRESS, result.getFhirResource().getStatus());

        checkDataElementCodeableConcept(result.getFhirResource().getCode());
        checkReason(result.getFhirResource().getReasonCodeFirstRep());
        checkRelevantPeriod(result.getFhirResource().getPerformedPeriod());
        checkPerformer(result.getFhirResource().getPerformerFirstRep().getActor());

        assertEquals(0, result.getConversionMessages().size());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        QdmToFhirConversionResult<Procedure> result = deviceAppliedConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        assertEquals(Procedure.ProcedureStatus.NOTDONE, result.getFhirResource().getStatus());

        assertEquals(0, result.getConversionMessages().size());

        assertEquals(1, result.getFhirResource().getModifierExtension().size());
        checkNotDoneExtension(result.getFhirResource().getModifierExtension().get(0));

        checkRecordedExtension(result.getFhirResource().getExtension().get(0));

    }

        @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<Procedure> result = deviceAppliedConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Procedure.ProcedureStatus.INPROGRESS, result.getFhirResource().getStatus());

        assertEquals(0, result.getConversionMessages().size());

    }
}