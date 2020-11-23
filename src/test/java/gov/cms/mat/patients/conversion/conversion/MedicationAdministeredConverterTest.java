package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MedicationAdministeredConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private MedicationAdministeredConverter medicationAdministeredConverter;

    @Test
    void getQdmType() {
        assertEquals(MedicationAdministeredConverter.QDM_TYPE, medicationAdministeredConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setDosage(createDosage());
        qdmDataElement.setRoute(createRoute());

        qdmDataElement.setFrequency(createFrequency()); //  not mapped

        qdmDataElement.setReason(createReason());
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setPerformer(createPerformer());

        QdmToFhirConversionResult<MedicationAdministration> result =
                medicationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);

        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        checkDataElementCodeableConcept(result.getFhirResource().getMedicationCodeableConcept());
        checkDosage(result.getFhirResource().getDosage().getDose());
        checkRoute(result.getFhirResource().getDosage().getRoute());
        checkReason(result.getFhirResource().getReasonCodeFirstRep());
        // checkRelevantDateTime(result.getFhirResource().getEffectiveDateTimeType().getValue());
        checkRelevantPeriod(result.getFhirResource().getEffectivePeriod());
        checkPerformer(result.getFhirResource().getPerformerFirstRep().getActor());

        assertEquals(MedicationAdministration.MedicationAdministrationStatus.UNKNOWN, result.getFhirResource().getStatus());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }

    @Test
    void convertToFhirSetEffectiveDate() {
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());

        QdmToFhirConversionResult<MedicationAdministration> result =
                medicationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkRelevantDateTime(result.getFhirResource().getEffectiveDateTimeType().getValue());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        QdmToFhirConversionResult<MedicationAdministration> result =
                medicationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        checkNegationRationaleTypeCodeableConcept(result.getFhirResource().getStatusReasonFirstRep());

        assertEquals(MedicationAdministration.MedicationAdministrationStatus.NOTDONE, result.getFhirResource().getStatus());

        checkRecordedExtension(result.getFhirResource().getExtension().get(0));

        assertEquals(0, result.getConversionMessages().size());
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<MedicationAdministration> result =
                medicationAdministeredConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(MedicationAdministration.MedicationAdministrationStatus.UNKNOWN, result.getFhirResource().getStatus());

        checkNoStatusMappingOnly(result.getConversionMessages());
    }
}