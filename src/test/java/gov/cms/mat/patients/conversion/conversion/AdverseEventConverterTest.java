package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.AdverseEvent;
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
class AdverseEventConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private AdverseEventConverter adverseEventConverter;

    @Test
    void getQdmType() {
        assertEquals(AdverseEventConverter.QDM_TYPE, adverseEventConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());

        QdmToFhirConversionResult<AdverseEvent> result = adverseEventConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkDataElementCodeableConcept(result.getFhirResource().getEvent());
        checkRelevantDateTime(result.getFhirResource().getDate());

        /// ensure not mapped
        assertFalse(result.getFhirResource().hasCategory());
        assertFalse(result.getFhirResource().hasSeverity());
        assertFalse(result.getFhirResource().hasLocation());
        assertNull(result.getFhirResource().getRecordedDate());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<AdverseEvent> result = adverseEventConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
    }

    @Test
    void convertToFhirNoDataElements() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));


        qdmDataElement.setType(createType());
        qdmDataElement.setSeverity(createSeverity());
        qdmDataElement.setFacilityLocations(List.of(createFacilityLocation()));
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        QdmToFhirConversionResult<AdverseEvent> result = adverseEventConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkTypeList(result.getFhirResource().getCategory());

    }


}