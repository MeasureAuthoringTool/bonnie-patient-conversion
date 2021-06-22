package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseConversionTest implements FhirConversionTest {
    protected static final String PATIENT_ID = "1";
    protected static final String ELEMENT_ID = "2";
    protected static final String FAMILY_NAME = "Public";
    protected static final String[] GIVEN_NAMES = {"Joe", "Q"};

    protected Patient fhirPatient;
    protected QdmDataElement qdmDataElement;

    @BeforeEach
    void before() {
        fhirPatient = createFhirPatient();
        qdmDataElement = createQdmDataElement();
    }

    @Test
    void verifyIds() {
        assertEquals(PATIENT_ID, fhirPatient.getId());
        assertEquals(ELEMENT_ID, qdmDataElement.getId());
    }
}
