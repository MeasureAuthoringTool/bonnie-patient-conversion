package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FamilyHistoryConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private FamilyHistoryConverter familyHistoryConverter;

    @Test
    void getQdmType() {
        assertEquals(FamilyHistoryConverter.QDM_TYPE, familyHistoryConverter.getQdmType());
    }

    @Test
    void convertToFhir() {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
        qdmDataElement.setRelationship(createRelationship());

        QdmToFhirConversionResult<FamilyMemberHistory> result = familyHistoryConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNotNull(result);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        checkDataElementCodeableConcept(result.getFhirResource().getConditionFirstRep().getCode());
        checkAuthorDatetime(result.getFhirResource().getDate());
        checkRelationShip(result.getFhirResource().getRelationship());

        assertEquals(FamilyMemberHistory.FamilyHistoryStatus.COMPLETED, result.getFhirResource().getStatus());

        assertTrue(result.getConversionMessages().isEmpty());
    }

    @Test
    void convertToFhirBadRelationShip() {

        qdmDataElement.setRelationship(createRelationshipNoSystem()); // adapts now

        QdmToFhirConversionResult<FamilyMemberHistory> result = familyHistoryConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());

        assertTrue(result.getFhirResource().hasRelationship());

        assertTrue(result.getConversionMessages().isEmpty());
    }

    private void checkRelationShip(CodeableConcept relationship) {
        checkSNOMEDCodeableConcept(relationship, "66839005", "Father");
    }

    private QdmCodeSystem createRelationship() {
        return createSNOMEDCode("66839005", "Father");
    }

    private QdmCodeSystem createRelationshipNoSystem() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setCode("AUNT");
        qdmCodeSystem.setDisplay("Family Member Value");
        return qdmCodeSystem;
    }

    @Test
    void convertToFhirEmptyObjects() {
        QdmToFhirConversionResult<FamilyMemberHistory> result = familyHistoryConverter.convertToFhir(fhirPatient, qdmDataElement);
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getPatient());
        assertNotNull(result);
        assertTrue(result.getConversionMessages().isEmpty());
    }
}