package gov.cms.mat.patients.conversion.conversion.helpers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPeriod;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPractitioner;
import gov.cms.mat.patients.conversion.dao.conversion.QdmQuantity;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_NOT_DONE;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_RECORDED;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UCUM_SYSTEM;

public interface FhirCreator {
    default Reference createPatientReference(Patient fhirPatient) {
        var reference = new Reference("Patient/" + fhirPatient.getId());
        return reference.setDisplay(convertHumanNamesToString(fhirPatient.getName()));
    }

    default Reference createPractitionerReference(QdmPractitioner qdmPractitioner) {
        return new Reference("Practitioner/" + qdmPractitioner.getId());

    }

    default CodeableConcept createCodeableConcept(QdmCodeSystem code, String system) {
        return createCodeableConcept(system, code.getCode(), code.getDisplay());
    }

    default CodeableConcept createCodeableConcept(String system, String code, String display) {
        return new CodeableConcept()
                .setCoding(Collections.singletonList(new Coding(system, code, display)));
    }

    default String convertHumanNamesToString(List<HumanName> humanNames) {
        if (CollectionUtils.isEmpty(humanNames)) {
            return "No Human Names Found";
        } else {
            var humanName = humanNames.get(0);

            String given = humanName.getGiven().stream()
                    .map(StringType::getValueNotNull)
                    .collect(Collectors.joining(" "));

            if (StringUtils.isEmpty(given)) {
                return humanName.getFamily();
            } else {
                return given + " " + humanName.getFamily();
            }
        }
    }

    default Period convertPeriod(QdmPeriod relevantPeriod) {
        if (relevantPeriod.getLow() != null)
            relevantPeriod.getLow().setPrecision(TemporalPrecisionEnum.MILLI);
        if (relevantPeriod.getHigh() != null)
            relevantPeriod.getHigh().setPrecision(TemporalPrecisionEnum.MILLI);
        return new Period()
                .setStartElement(relevantPeriod.getLow())
                .setEndElement(relevantPeriod.getHigh());
    }

    default String toJson(FhirContext fhirContext, IBaseResource theResource) {
        return fhirContext.newJsonParser()
                .setPrettyPrint(true)
                .encodeResourceToString(theResource);
    }

    default Quantity convertQuantity(QdmQuantity qdmQuantity) {
        return createQuantity(qdmQuantity.getValue(), qdmQuantity.getUnit());
    }

    default Quantity createQuantity(Integer value, String unit) {
        var quantity = new Quantity();

        if (value != null) {
            quantity.setValue(value);
        }

        quantity.setSystem(UCUM_SYSTEM);

        quantity.setCode(convertUnitToCode(unit));

        return quantity;
    }

    default String convertUnitToCode(String unit) {
        // https://ucum.nlm.nih.gov/ucum-lhc/demo.html Nice tool for codes
        // Let bonnie decide what valid for now and do not validate
        return unit;
    }

    default List<Reference> convertRelatedTo(List<String> relatedTo) {
        return relatedTo.stream()
                .map(relatedToId -> new Reference("Unknown/" + relatedToId))
                .collect(Collectors.toList());
    }

    default Extension createNotDoneExtension() {
        return new Extension(QICORE_NOT_DONE)
                .setValue(new BooleanType(true));
    }

    default Extension createRecordedExtension(DateTimeType date) {
        date.setPrecision(TemporalPrecisionEnum.MILLI);
        return new Extension(QICORE_RECORDED)
                .setValue(date);
    }

    default Set<String> collectQdmTypes(BonniePatient bonniePatient) {
        if (bonniePatient.getQdmPatient() == null || org.apache.commons.collections4.CollectionUtils.isEmpty(bonniePatient.getQdmPatient().getDataElements())) {
            return Collections.emptySet();
        } else {
            return bonniePatient.getQdmPatient().getDataElements().stream()
                    .map(QdmDataElement::getQdmType)
                    .collect(Collectors.toSet());
        }
    }
}
