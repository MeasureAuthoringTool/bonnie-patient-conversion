package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.MedicationRequestTest;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ImmunizationAdministeredNegationConverterTest extends BaseConversionTest implements FhirConversionTest, MedicationRequestTest {
    @Autowired
    private ImmunizationAdministeredNegationConverter immunizationAdministeredNegationConverter;

    @Test
    void getQdmType() {
        assertEquals(ImmunizationAdministeredNegationConverter.QDM_TYPE, immunizationAdministeredNegationConverter.getQdmType());
    }

    @Test
    void convertToFhirNegation() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());

        checkNegation(result);
    }

    @Test
    void convertToFhirNegationEmptyObjects() {
        qdmDataElement.setNegationRationale(createNegationRationale());

        createMedicationRequestElement(qdmDataElement);

        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);

        assertEquals(MedicationRequest.MedicationRequestIntent.ORDER, result.getFhirResource().getIntent());
        assertEquals(2, result.getFhirResource().getReasonCode().size());
        checkNegation(result);

        checkMedicationRequest(result);
    }

    @Test
    void convertToFhirEmptyObjects() {
        // convert only processes if negation rational is present
        QdmToFhirConversionResult<MedicationRequest> result = immunizationAdministeredNegationConverter.convertToFhir(fhirPatient, qdmDataElement);
        assertNull(result);
    }

}