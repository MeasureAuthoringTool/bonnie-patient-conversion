package gov.cms.mat.patients.conversion.conversion;

import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseConverterTest {
    static final String PATIENT_ID = "1";
    static final String ELEMENT_ID = "2";
    static final String FAMILY_NAME = "Public";
    static final String GIVEN_NAMES[] = {"Joe", "Q"};
    private final static Instant now = Instant.now();

    private HumanName createName() {
        HumanName humanName = new HumanName();
        humanName.setUse(HumanName.NameUse.USUAL);

        humanName.setFamily(FAMILY_NAME);

        List<StringType> fhirNames = Arrays.stream(GIVEN_NAMES)
                .map(StringType::new)
                .collect(Collectors.toList());

        humanName.setGiven(fhirNames);

        return humanName;
    }

    Patient createFhirPatient() {
        Patient patient = new Patient();
        patient.setName(List.of(createName()));
        patient.setId(PATIENT_ID);
        return patient;
    }

    QdmDataElement createQdmDataElement() {
        QdmDataElement qdmDataElement = new QdmDataElement();
        qdmDataElement.set_id(ELEMENT_ID);
        return qdmDataElement;
    }

    public void checkBase(String fhirResourceId, Reference patientReference) {
        assertEquals(fhirResourceId, ELEMENT_ID);

        assertEquals(patientReference.getReference(), "Patient/1");
        assertEquals(patientReference.getDisplay(), "Joe Q Public");
    }

    public QdmCodeSystem createDataElementCode() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("50699000");
        qdmCodeSystem.setDisplay("Hospital admission, short-term");
        return qdmCodeSystem;
    }

    public void checkDataElementCode(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("50699000", coding.getCode());
        assertEquals("Hospital admission, short-term", coding.getDisplay());
    }

    public Date createRelevantDatetime() {
        return new Date(now.toEpochMilli() - 10000);
    }

    public void checkRelevantDateTime(Date relevantDatetime) {
        assertEquals(createRelevantDatetime(), relevantDatetime);
    }

    public QdmPeriod createPrevalencePeriod() {
        QdmPeriod qdmPeriod = new QdmPeriod();
        qdmPeriod.setLow(new Date(now.toEpochMilli() - 1000000));
        qdmPeriod.setHigh(new Date(now.toEpochMilli() - 100));
        return qdmPeriod;
    }

    public void checkPrevalencePeriod(Period fhirPeriod) {
        QdmPeriod qdmPeriod = createPrevalencePeriod();

        assertEquals(qdmPeriod.getLow(), fhirPeriod.getStart());
        assertEquals(qdmPeriod.getHigh(), fhirPeriod.getEnd());
    }

    public Date createAuthorDatetime() {
        return new Date(now.toEpochMilli() - 50);
    }

    public void checkAuthorDatetime(Date date) {
        assertEquals(createAuthorDatetime(), date);
    }


    public QdmCodeSystem createType() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("64572001");
        qdmCodeSystem.setDisplay("Diseases");
        return qdmCodeSystem;
    }

    public void checkType(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("64572001", coding.getCode());
        assertEquals("Diseases", coding.getDisplay());
    }

    public QdmCodeSystem createReason() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("80247002");
        qdmCodeSystem.setDisplay("Third degree burn injury");
        return qdmCodeSystem;
    }

    public void checkReason(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("80247002", coding.getCode());
        assertEquals("Third degree burn injury", coding.getDisplay());
    }

    public QdmCodeSystem createNegationRationale() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("47448006");
        qdmCodeSystem.setDisplay("Hot Water");
        return qdmCodeSystem;
    }

    public void checkNegationRationale(Type value) {
        assertThat(value, instanceOf(Coding.class));
        Coding coding = (Coding) value;

        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("47448006", coding.getCode());
        assertEquals("Hot Water", coding.getDisplay());
    }
}
