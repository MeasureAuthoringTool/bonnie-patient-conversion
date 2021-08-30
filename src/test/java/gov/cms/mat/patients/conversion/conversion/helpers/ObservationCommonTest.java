package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmQuantity;
import gov.cms.mat.patients.conversion.dao.conversion.QdmReferenceRange;
import org.hl7.fhir.r4.model.*;

import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_NOT_DONE;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.QICORE_NOT_DONE_REASON;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface ObservationCommonTest extends FhirConversionTest {
    default void checkDataElement(QdmToFhirConversionResult<Observation> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());
        checkDataElementCodeableConcept(result.getFhirResource().getCode());

        checkComponents(result.getFhirResource().getComponent());

        checkRelevantPeriod((Period) result.getFhirResource().getEffective());

        checkMethod(result.getFhirResource().getMethod());

        checkReferenceRange(result.getFhirResource().getReferenceRangeFirstRep());
    }


    default void createObservationDataElement(QdmDataElement qdmDataElement) {

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));

        // if relevantPeriod & relevantDatetime both exist, period will be mapped
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());

        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        qdmDataElement.setComponents(List.of(createComponents()));

        qdmDataElement.setReason(createReason());

        qdmDataElement.setMethod(createMethod());

        qdmDataElement.setReferenceRange(createReferenceRange());
    }

    private QdmReferenceRange createReferenceRange() {
        QdmReferenceRange qdmReferenceRange = new QdmReferenceRange();

        qdmReferenceRange.setHigh(createQuantityHigh());
        qdmReferenceRange.setLow(createQuantityLow());

        return qdmReferenceRange;
    }

    private QdmQuantity createQuantityHigh() {
        return createQdmQuantity("g", 7000);
    }

    private QdmQuantity createQuantityLow() {
        return createQdmQuantity("mg", 700);
    }

    default void checkReferenceRange(Observation.ObservationReferenceRangeComponent rangeComponent) {
        assertEquals(7000, rangeComponent.getHigh().getValue().intValue());
        assertEquals("g", rangeComponent.getHigh().getCode());

        assertEquals(700, rangeComponent.getLow().getValue().intValue());
        assertEquals("mg", rangeComponent.getLow().getCode());
    }


    default void checkWithoutNegationResult(QdmToFhirConversionResult<Observation> result) {
        checkDataElement(result);
        assertEquals(Observation.ObservationStatus.FINAL, result.getFhirResource().getStatus());
    }

    default void checkNegationResult(QdmToFhirConversionResult<Observation> result) {
        checkDataElement(result);
        assertEquals(Observation.ObservationStatus.FINAL, result.getFhirResource().getStatus());

        assertEquals(1, result.getFhirResource().getExtension().size());
        Extension extension = result.getFhirResource().getExtensionByUrl(QICORE_NOT_DONE_REASON);
        checkNegationRationaleType(extension.getValue());

        assertEquals(1, result.getFhirResource().getModifierExtension().size());
        Extension modifierExtension = result.getFhirResource().getModifierExtensionsByUrl(QICORE_NOT_DONE).get(0);
        assertThat(modifierExtension.getValue(), instanceOf(BooleanType.class));
        assertTrue(((BooleanType) modifierExtension.getValue()).booleanValue());
    }
}
