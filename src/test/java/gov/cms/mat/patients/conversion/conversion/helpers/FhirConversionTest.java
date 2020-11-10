package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import gov.cms.mat.patients.conversion.dao.conversion.FacilityLocation;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCode;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmComponent;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
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
}
