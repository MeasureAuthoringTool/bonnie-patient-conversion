package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Period;

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
        checkDataElementCode(result.getFhirResource().getCode());

        checkRelevantPeriod((Period) result.getFhirResource().getEffective());
        checkAuthorDatetime(result.getFhirResource().getIssued());
        checkComponents(result.getFhirResource().getComponent());
    }

    default void createObservationDataElement(QdmDataElement qdmDataElement) {

        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRelevantPeriod(createRelevantPeriod());

        qdmDataElement.setAuthorDatetime(createAuthorDatetime());

        qdmDataElement.setComponents(List.of(createComponents()));
    }

    default void checkWithoutNegationResult(QdmToFhirConversionResult<Observation> result) {
        checkDataElement(result);
        assertEquals(Observation.ObservationStatus.UNKNOWN, result.getFhirResource().getStatus());
    }

    default void checkNegationResult(QdmToFhirConversionResult<Observation> result) {
        checkDataElement(result);
        assertEquals(Observation.ObservationStatus.FINAL, result.getFhirResource().getStatus());

        assertEquals(1, result.getFhirResource().getExtension().size());
        Extension extension = result.getFhirResource().getExtensionByUrl(QICORE_NOT_DONE_REASON);
        checkNegationRationale(extension.getValue());

        assertEquals(1, result.getFhirResource().getModifierExtension().size());
        Extension modifierExtension = result.getFhirResource().getModifierExtensionsByUrl(QICORE_NOT_DONE).get(0);
        assertThat(modifierExtension.getValue(), instanceOf(BooleanType.class));
        assertTrue(((BooleanType) modifierExtension.getValue()).booleanValue());
    }
}
