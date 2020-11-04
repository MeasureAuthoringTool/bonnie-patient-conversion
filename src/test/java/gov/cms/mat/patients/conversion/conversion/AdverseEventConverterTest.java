package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.AdverseEvent;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class AdverseEventConverterTest extends BaseConverterTest {
    @Autowired
    private AdverseEventConverter adverseEventConverter;

    @Test
    void getQdmType() {
        assertEquals(AdverseEventConverter.QDM_TYPE, adverseEventConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createQdmDataElement();

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());

        QdmToFhirConversionResult<AdverseEvent> result = adverseEventConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkDataElementCode(result.getFhirResource().getEvent());
        checkRelevantDateTime(result.getFhirResource().getDate());

        /// ensure not mapped
        assertFalse(result.getFhirResource().hasCategory());
        assertFalse(result.getFhirResource().hasSeverity());
        assertFalse(result.getFhirResource().hasLocation());
        assertNull(result.getFhirResource().getRecordedDate());
    }

    @Test
    void convertToFhirEmptyObjects() {
        Patient fhirPatient = createFhirPatient();
        QdmDataElement qdmDataElement = createQdmDataElement();
        QdmToFhirConversionResult<AdverseEvent> result = adverseEventConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }
}