package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import gov.cms.mat.patients.conversion.dao.conversion.FacilityLocation;
import gov.cms.mat.patients.conversion.dao.conversion.LengthOfStay;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCode;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmComponent;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPractitioner;
import gov.cms.mat.patients.conversion.dao.conversion.QdmQuantity;
import gov.cms.mat.patients.conversion.dao.conversion.TargetOutcome;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_NOT_DONE;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_RECORDED;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.SNOMED_OID;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.ELEMENT_ID;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.FAMILY_NAME;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.GIVEN_NAMES;
import static gov.cms.mat.patients.conversion.conversion.helpers.BaseConversionTest.PATIENT_ID;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface FhirConversionTest {
    Instant now = Instant.now();

    default void checkSNOMEDCodeableConcept(CodeableConcept codeableConcept, String code, String display) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);

        checkSNOMEDCoding(coding, code, display);
    }

    default void checkSNOMEDCoding(Coding coding, String code, String display) {
        assertEquals("http://snomed.info/sct", coding.getSystem());
        assertEquals(code, coding.getCode());
        assertEquals(display, coding.getDisplay());
    }

    default QdmCodeSystem createSNOMEDCode(String code, String display) {
        QdmCodeSystem qdmCodeSystem = new QdmCodeSystem();
        qdmCodeSystem.setSystem(SNOMED_OID);
        qdmCodeSystem.setCode(code);
        qdmCodeSystem.setDisplay(display);
        return qdmCodeSystem;
    }

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
        return createSNOMEDCode("50699000", "Hospital admission, short-term");
    }

    default void checkDataElementCodeableConcept(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "50699000", "Hospital admission, short-term");
    }

    default void checkDataElementCoding(Coding coding) {
        checkSNOMEDCoding(coding, "50699000", "Hospital admission, short-term");
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

    default QdmPeriod createParticipationPeriod() {
        QdmPeriod qdmPeriod = new QdmPeriod();
        qdmPeriod.setLow(new Date(now.toEpochMilli() - 1000000));
        qdmPeriod.setHigh(new Date(now.toEpochMilli() - 100));
        return qdmPeriod;
    }

    default void checkParticipationPeriod(Period fhirPeriod) {
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
        return createSNOMEDCode("64572001", "Diseases");
    }

    default void checkTypeList(List<CodeableConcept> codeableConcepts) {
        assertEquals(1, codeableConcepts.size());
        checkType(codeableConcepts.get(0));
    }

    default void checkType(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "64572001", "Diseases");
    }

    default QdmCodeSystem createReason() {
        return createSNOMEDCode("80247002", "Third degree burn injury");
    }

    default void checkReason(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "80247002", "Third degree burn injury");
    }

    default QdmCodeSystem createNegationRationale() {
        return createSNOMEDCode("47448006", "Hot Water");
    }

    default void checkNegationRationaleType(Type value) {
        assertThat(value, instanceOf(Coding.class));
        Coding coding = (Coding) value;

        checkSNOMEDCoding(coding, "47448006", "Hot Water");
    }

    default void checkNegationRationaleTypeCodeableConcept(CodeableConcept codeableConcept) {
        assertEquals(1, codeableConcept.getCoding().size());
        Coding coding = codeableConcept.getCoding().get(0);

        checkNegationRationaleType(coding);
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
        qdmCode.setSystem(SNOMED_OID);
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
        return createSNOMEDCode("24484000", "Severe");
    }

    default void checkSeverity(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "24484000", "Severe");
    }

    default FacilityLocation createFacilityLocation() {
        return new FacilityLocation();
    }

    default ObjectNode createCodeableConceptObjectNode() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();

        root.set("system", mapper.convertValue(SNOMED_OID, JsonNode.class));
        root.set("code", mapper.convertValue("276333003", JsonNode.class));
        root.set("display", mapper.convertValue("Microphallus", JsonNode.class));

        return root;
    }

    default void checkCodeableConceptObjectNode(Type value) {
        assertThat(value, instanceOf(CodeableConcept.class));

        CodeableConcept codeableConcept = (CodeableConcept) value;

        Coding coding = codeableConcept.getCodingFirstRep();

        checkSNOMEDCoding(coding, "276333003", "Microphallus");
    }

    default TargetOutcome createTargetOutcome() {
        TargetOutcome targetOutcome = new TargetOutcome();

        targetOutcome.setSystem(SNOMED_OID);
        targetOutcome.setCode("63901009");
        targetOutcome.setDisplay("Pain in testicle");
        return targetOutcome;
    }

    default void checkTargetOutCome(CodeableConcept codeableConcept) {
        Coding coding = codeableConcept.getCodingFirstRep();
        checkSNOMEDCoding(coding, "63901009", "Pain in testicle");
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

        QdmCodeSystem role = createSNOMEDCode("158965000", "Medical practitioner");
        qdmPractitioner.setRole(role);

        QdmCodeSystem specialty = createSNOMEDCode("309338004", "Intensive care specialist");
        qdmPractitioner.setSpecialty(specialty);

        QdmCodeSystem qualification = createSNOMEDCode("164618002", "General sign qualifications");
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

    default QdmPractitioner createPrescriber() {
        QdmPractitioner qdmPractitioner = new QdmPractitioner();
        qdmPractitioner.setId("9012345678");

        return qdmPractitioner;
    }

    default void checkRecipient(Reference reference) {
        assertEquals("Practitioner/7890123456", reference.getReference());
    }

    default void checkPerformer(Reference reference) {
        assertEquals("Practitioner/987654321", reference.getReference());
    }

    default QdmCodeSystem createCategory() {
        return createSNOMEDCode("183095004", "Usual warning given");
    }

    default QdmCodeSystem createMedium() {
        return createSNOMEDCode("408563008", "Email sent to consultant (finding)");
    }

    default void checkMedium(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "408563008", "Email sent to consultant (finding)");
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

    default void checkNotDoneExtension(Extension extension) {
        assertThat(extension.getValue(), instanceOf(BooleanType.class));
        BooleanType booleanType = (BooleanType) extension.getValue();

        assertTrue(booleanType.booleanValue());

        assertEquals(QICORE_NOT_DONE, extension.getUrl());
    }

    default void checkRecordedExtension(Extension extension) {
        assertThat(extension.getValue(), instanceOf(DateTimeType.class));
        DateTimeType dateTimeType = (DateTimeType) extension.getValue();

        assertEquals(createAuthorDatetime(), dateTimeType.getValue());

        assertEquals(QICORE_RECORDED, extension.getUrl());
    }

    default LengthOfStay createLengthOfStay() {
        LengthOfStay lengthOfStay = new LengthOfStay();
        lengthOfStay.setUnit("day");
        lengthOfStay.setValue(99);
        return lengthOfStay;
    }

    default void checkLengthOfStay(Duration duration) {
        assertEquals("day", duration.getUnit());
        assertEquals(99, duration.getValue().intValue());
    }

    default QdmCodeSystem createDischargeDisposition() {
        return createSNOMEDCode("430567009", "Ready for discharge");
    }

    default void checkDischargeDisposition(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "430567009", "Ready for discharge");
    }

    default QdmQuantity createDosage() {
        QdmQuantity qdmQuantity = new QdmQuantity();
        qdmQuantity.setUnit("mg");
        qdmQuantity.setValue(1000);
        return qdmQuantity;
    }

    default void checkDosage(Quantity doseQuantity) {
        assertEquals("mg", doseQuantity.getCode());
        assertEquals(1000, doseQuantity.getValue().intValue());
    }


    default QdmCodeSystem createRoute() {
        return createSNOMEDCode("430567009", "Buccal route");
    }

    default void checkRoute(CodeableConcept route) {
        checkSNOMEDCodeableConcept(route, "430567009", "Buccal route");
    }

    default QdmQuantity createSupply() {
        QdmQuantity qdmQuantity = new QdmQuantity();
        qdmQuantity.setUnit("g");
        qdmQuantity.setValue(100);
        return qdmQuantity;
    }

    default void checkSupply(Quantity quantity) {
        assertEquals(UCUM_SYSTEM, quantity.getSystem());
        assertEquals("g", quantity.getCode());
        assertEquals(100, quantity.getValue().intValue());
    }

    default QdmCodeSystem createFrequency() {
        return createSNOMEDCode("229799001", "Twice a day");
    }

    default void checkFrequency(CodeableConcept codeableConcept) {
        checkSNOMEDCodeableConcept(codeableConcept, "229799001", "Twice a day");
    }

    default QdmCodeSystem createSetting() {
        return createSNOMEDCode("450511000124101", "High intensity");
    }

    default void checkNoStatusMappingOnly(List<String> conversionMessages) {
        assertEquals(1, conversionMessages.size());
        assertEquals(NO_STATUS_MAPPING, conversionMessages.get(0));
    }

    default Date createIncisionDatetime() {
        return new Date(now.toEpochMilli() - 2222);
    }

    default void checkIncisionDatetime(Date date) {
        assertEquals(createIncisionDatetime(), date);
    }
}
