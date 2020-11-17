package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import gov.cms.mat.patients.conversion.dao.conversion.FacilityLocation;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCode;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmComponent;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPractitioner;
import gov.cms.mat.patients.conversion.dao.conversion.TargetOutcome;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Observation;
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

import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.ELEMENT_ID;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.FAMILY_NAME;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.GIVEN_NAMES;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.PATIENT_ID;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface FhirConversionTest {
    Instant now = Instant.now();

    default Patient createFhirPatient() {
        Patient patient = new Patient();
        patient.setName(List.of(createName()));
        patient.setId(PATIENT_ID);
        return patient;
    }

    default HumanName createName() {
        HumanName humanName = new HumanName();
        humanName.setUse(HumanName.NameUse.USUAL);

        humanName.setFamily(FAMILY_NAME);

        List<StringType> fhirNames = Arrays.stream(GIVEN_NAMES)
                .map(StringType::new)
                .collect(Collectors.toList());

        humanName.setGiven(fhirNames);

        return humanName;
    }

    default QdmDataElement createQdmDataElement() {
        QdmDataElement qdmDataElement = new QdmDataElement();
        qdmDataElement.setId(ELEMENT_ID);
        return qdmDataElement;
    }

    default void checkBase(String fhirResourceId, Reference patientReference) {
        assertEquals(ELEMENT_ID, fhirResourceId);

        assertEquals("Patient/" + PATIENT_ID, patientReference.getReference());

        String name = GIVEN_NAMES[0] + ' ' + GIVEN_NAMES[1] + ' ' + FAMILY_NAME;
        assertEquals(name, patientReference.getDisplay());
    }

    default QdmCodeSystem createDataElementCode() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("50699000");
        qdmCodeSystem.setDisplay("Hospital admission, short-term");
        return qdmCodeSystem;
    }

    default void checkDataElementCode(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("50699000", coding.getCode());
        assertEquals("Hospital admission, short-term", coding.getDisplay());
    }

    default Date createRelevantDatetime() {
        return new Date(now.toEpochMilli() - 10000);
    }

    default void checkRelevantDateTime(Date relevantDatetime) {
        assertEquals(createRelevantDatetime(), relevantDatetime);
    }

    default QdmPeriod createPrevalencePeriod() {
        QdmPeriod qdmPeriod = new QdmPeriod();
        qdmPeriod.setLow(new Date(now.toEpochMilli() - 1000000));
        qdmPeriod.setHigh(new Date(now.toEpochMilli() - 100));
        return qdmPeriod;
    }

    default void checkPrevalencePeriod(Period fhirPeriod) {
        QdmPeriod qdmPeriod = createPrevalencePeriod();

        assertEquals(qdmPeriod.getLow(), fhirPeriod.getStart());
        assertEquals(qdmPeriod.getHigh(), fhirPeriod.getEnd());
    }

    default Date createAuthorDatetime() {
        return new Date(now.toEpochMilli() - 50);
    }

    default void checkAuthorDatetime(Date date) {
        assertEquals(createAuthorDatetime(), date);
    }


    default QdmCodeSystem createType() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("64572001");
        qdmCodeSystem.setDisplay("Diseases");
        return qdmCodeSystem;
    }

    default void checkTypeList(List<CodeableConcept> codeableConcepts) {
        assertEquals(1, codeableConcepts.size());
        checkType(codeableConcepts.get(0));
    }

    default void checkType(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("64572001", coding.getCode());
        assertEquals("Diseases", coding.getDisplay());
    }

    default QdmCodeSystem createReason() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("80247002");
        qdmCodeSystem.setDisplay("Third degree burn injury");
        return qdmCodeSystem;
    }

    default void checkReason(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("80247002", coding.getCode());
        assertEquals("Third degree burn injury", coding.getDisplay());
    }

    default QdmCodeSystem createNegationRationale() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("47448006");
        qdmCodeSystem.setDisplay("Hot Water");
        return qdmCodeSystem;
    }

    default void checkNegationRationale(Type value) {
        assertThat(value, instanceOf(Coding.class));
        Coding coding = (Coding) value;

        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("47448006", coding.getCode());
        assertEquals("Hot Water", coding.getDisplay());
    }

    default IntNode createIntegerTypeResult() {
        return new IntNode(Integer.MAX_VALUE);
    }

    default DoubleNode createDoubleTypeResult() {
        return new DoubleNode(22.2);
    }

    default void checkIntegerTypeResult(Type result) {
        assertThat(result, instanceOf(IntegerType.class));

        IntegerType integerType = (IntegerType) result;

        assertEquals(Integer.MAX_VALUE, integerType.getValue());
    }

    default TextNode createTextTypeResult() {
        return new TextNode("FHIR");
    }

    default void checkTextTypeResult(Type result) {
        assertThat(result, instanceOf(StringType.class));

        StringType stringType = (StringType) result;

        assertEquals("FHIR", stringType.getValue());
    }

    default TextNode createTextTypeResultDate() {
        return new TextNode("2012-02-01T19:00:00.000+00:00");
    }

    default void checkTextTypeResultDate(Type result) {
        assertThat(result, instanceOf(DateTimeType.class));

        DateTimeType dateTimeType = (DateTimeType) result;

        assertEquals("2012-02-01T19:00:00+00:00", dateTimeType.getValueAsString());
    }

    default BooleanNode createBooleanTypeResult() {
        return BooleanNode.FALSE;
    }

    default QdmPeriod createRelevantPeriod() {
        QdmPeriod qdmPeriod = new QdmPeriod();
        qdmPeriod.setLow(new Date(now.toEpochMilli() - 2000000));
        qdmPeriod.setHigh(new Date(now.toEpochMilli() - 1000));
        return qdmPeriod;
    }

    default void checkRelevantPeriod(Period fhirPeriod) {
        QdmPeriod qdmPeriod = createRelevantPeriod();

        assertEquals(qdmPeriod.getLow(), fhirPeriod.getStart());
        assertEquals(qdmPeriod.getHigh(), fhirPeriod.getEnd());
    }

    default void checkRelevantPeriodGoal(Type start, DateType end) {
        assertThat(start, instanceOf(DateType.class));
        DateType dateType = (DateType) start;
        assertEquals(new Date(now.toEpochMilli() - 2000000), dateType.getValue());

        assertEquals(new Date(now.toEpochMilli() - 1000), end.getValue());
    }

    default QdmComponent createComponents() {

        QdmComponent qdmComponent = new QdmComponent();

        QdmCode qdmCode = new QdmCode();
        qdmCode.setSystem("2.16.840.1.113883.6.96");
        qdmCode.setCode("11713004");
        qdmCode.setDisplay("H2O - water");
        qdmComponent.setCode(qdmCode);

        qdmComponent.setResult(new IntNode(Integer.MIN_VALUE));


        QdmPeriod qdmPeriod = new QdmPeriod();
        qdmPeriod.setLow(new Date(now.toEpochMilli() - 20000));
        qdmPeriod.setHigh(new Date(now.toEpochMilli() - 999));

        qdmComponent.setReferenceRange(qdmPeriod);

        return qdmComponent;
    }

    default void checkComponents(List<Observation.ObservationComponentComponent> components) {
        assertEquals(1, components.size());
        //todo
    }

    default QdmCodeSystem createSeverity() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("24484000");
        qdmCodeSystem.setDisplay("Severe");
        return qdmCodeSystem;
    }

    default void checkSeverity(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("24484000", coding.getCode());
        assertEquals("Severe", coding.getDisplay());
    }

    default FacilityLocation createFacilityLocation() {
        return new FacilityLocation();
    }

    default ObjectNode createCodeableConceptObjectNode() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();

        root.set("system", mapper.convertValue("2.16.840.1.113883.6.96", JsonNode.class));
        root.set("code", mapper.convertValue("276333003", JsonNode.class));
        root.set("display", mapper.convertValue("Microphallus", JsonNode.class));

        return root;
    }

    default void checkCodeableConceptObjectNode(Type value) {
        assertThat(value, instanceOf(CodeableConcept.class));

        CodeableConcept codeableConcept = (CodeableConcept) value;

        Coding coding = codeableConcept.getCodingFirstRep();

        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("276333003", coding.getCode());
        assertEquals("Microphallus", coding.getDisplay());
    }

    default TargetOutcome createTargetOutcome() {
        TargetOutcome targetOutcome = new TargetOutcome();

        targetOutcome.setSystem("2.16.840.1.113883.6.96");
        targetOutcome.setCode("63901009");
        targetOutcome.setDisplay("Pain in testicle");
        return targetOutcome;
    }

    default void checkTargetOutCome(CodeableConcept codeableConcept) {
        Coding coding = codeableConcept.getCodingFirstRep();

        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("63901009", coding.getCode());
        assertEquals("Pain in testicle", coding.getDisplay());
    }

    default String createRelatedTo() {
        return "1234567890";
    }

    default void checkRelatedTo(Reference reference) {
        assertEquals("Unknown/1234567890", reference.getReference());
    }

    default QdmPractitioner createPerformer() {
        QdmPractitioner qdmPractitioner = new QdmPractitioner();
        qdmPractitioner.setId("987654321");

        QdmCodeSystem role = new QdmCodeSystem();
        role.setSystem("2.16.840.1.113883.6.96");
        role.setCode("158965000");
        role.setDisplay("Medical practitioner");
        qdmPractitioner.setRole(role);

        QdmCodeSystem specialty = new QdmCodeSystem();
        specialty.setSystem("2.16.840.1.113883.6.96");
        specialty.setCode("309338004");
        specialty.setDisplay("Intensive care specialist");
        qdmPractitioner.setSpecialty(specialty);

        QdmCodeSystem qualification = new QdmCodeSystem();
        qualification.setSystem("2.16.840.1.113883.6.96");
        qualification.setCode("164618002");
        qualification.setDisplay("General sign qualifications");
        qdmPractitioner.setQualification(qualification);

        return qdmPractitioner;
    }

    default QdmPractitioner createSender() {
        QdmPractitioner qdmPractitioner = new QdmPractitioner();
        qdmPractitioner.setId("54678901234");

        return qdmPractitioner;
    }

    default void checkSender(Reference reference) {
        assertEquals("Practitioner/54678901234", reference.getReference());
    }

    default QdmPractitioner createRecipient() {
        QdmPractitioner qdmPractitioner = new QdmPractitioner();
        qdmPractitioner.setId("7890123456");

        return qdmPractitioner;
    }

    default void checkRecipient(Reference reference) {
        assertEquals("Practitioner/7890123456", reference.getReference());
    }

    default void checkPerformer(Reference reference) {
        assertEquals("Practitioner/987654321", reference.getReference());
    }

    default QdmCodeSystem createCategory() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("183095004");
        qdmCodeSystem.setDisplay("Usual warning given");
        return qdmCodeSystem;
    }

    default QdmCodeSystem createMedium() {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem("2.16.840.1.113883.6.96");
        qdmCodeSystem.setCode("408563008");
        qdmCodeSystem.setDisplay("Email sent to consultant (finding)");
        return qdmCodeSystem;
    }

    default void checkMedium(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals("408563008", coding.getCode());
        assertEquals("Email sent to consultant (finding)", coding.getDisplay());
    }

    default Date createSentDatetime() {
        return new Date(now.toEpochMilli() - 12345);
    }

    default void checkSentDatetime(Date sent) {
        assertEquals(createSentDatetime(), sent);
    }

    default Date createReceivedDatetime() {
        return new Date(now.toEpochMilli() - 1);
    }

    default void checkReceivedDatetime(Date received) {
        assertEquals(createReceivedDatetime(), received);
    }

}
