package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirConversionTest;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPatient;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PractitionerConverterTest extends BaseConversionTest implements FhirConversionTest {
    @Autowired
    private PractitionerConverter practitionerConverter;
    private BonniePatient bonniePatient;

    @BeforeEach
    void beforeBase() {
        bonniePatient = new BonniePatient();
        bonniePatient.setQdmPatient(new QdmPatient());
        bonniePatient.getQdmPatient().setDataElements(new ArrayList<>());
    }

    private QdmDataElement createAndAddElement(String id) {
        QdmDataElement senderElement = new QdmDataElement();
        senderElement.setDescription(id);
        qdmDataElement.setId(id);
        bonniePatient.getQdmPatient().getDataElements().add(senderElement);
        return senderElement;
    }

    @Test
    void convertToFhir() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> practitionerConverter.convertToFhir(null, null));
    }


    @Test
    void createDataElements() {
        createAndAddElement("PERFORMER").setPerformer(createPerformer());

        createAndAddElement("SENDER").setSender(createSender());
        createAndAddElement("RECIPIENT").setRecipient(createRecipient());
        createAndAddElement("DISPENSER").setDispenser(createDispenser());
        createAndAddElement("PRESCRIBER").setPrescriber(createPrescriber());

        List<FhirDataElement> elementList = practitionerConverter.createDataElements(bonniePatient, fhirPatient);

        assertEquals(5, elementList.size());

        var optional = elementList.stream()
                .filter(p -> p.getValueSetTitle().equals("PERFORMER"))
                .findFirst();

        assertTrue(optional.isPresent());

        checkPerformer(optional.get());
    }

    private void checkPerformer(FhirDataElement fhirDataElement) {
        assertEquals(0, fhirDataElement.getOutcome().getConversionMessages().size());
        assertEquals(0, fhirDataElement.getOutcome().getValidationMessages().size());
        assertThat(fhirDataElement.getFhirObject(), instanceOf(Practitioner.class));

        Practitioner practitioner = (Practitioner) fhirDataElement.getFhirObject();

        assertEquals(3, practitioner.getQualification().size());

        checkRole(practitioner.getQualification().get(0).getCode());
        checkSpecialty(practitioner.getQualification().get(1).getCode());
        checkQualification(practitioner.getQualification().get(2).getCode());

        checkIdentifier(practitioner.getIdentifierFirstRep());
    }


    @Test
    void createDataElementsNoDataToProcess() {
        bonniePatient.setQdmPatient(null);
        List<FhirDataElement> elementList = practitionerConverter.createDataElements(bonniePatient, fhirPatient);
        assertTrue(elementList.isEmpty());

        bonniePatient.setQdmPatient(new QdmPatient());

        elementList = practitionerConverter.createDataElements(bonniePatient, fhirPatient);
        assertTrue(elementList.isEmpty());
    }

    @Test
    void getQdmType() {
        assertNull(practitionerConverter.getQdmType());
    }
}