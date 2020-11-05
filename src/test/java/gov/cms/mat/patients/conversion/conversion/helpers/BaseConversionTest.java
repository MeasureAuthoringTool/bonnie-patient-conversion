package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;

public class BaseConversionTest implements FhirConversionTest {
    static final String PATIENT_ID = "1";
    static final String ELEMENT_ID = "2";
    static final String FAMILY_NAME = "Public";
    static final String[] GIVEN_NAMES = {"Joe", "Q"};

    protected Patient fhirPatient;
    protected QdmDataElement qdmDataElement;

    @BeforeEach
    void before() {
        fhirPatient = createFhirPatient();
        qdmDataElement = createQdmDataElement();
    }
}
