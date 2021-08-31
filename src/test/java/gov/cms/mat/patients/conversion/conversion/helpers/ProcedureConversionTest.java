package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.node.BooleanNode;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Procedure;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ProcedureConversionTest extends FhirConversionTest {
    default void createNegation(QdmDataElement qdmDataElement) {
        qdmDataElement.setNegationRationale(createNegationRationale());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
    }

    default void checkNegation(QdmToFhirConversionResult<Procedure> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(0, result.getConversionMessages().size());
        assertEquals(Procedure.ProcedureStatus.NOTDONE, result.getFhirResource().getStatus());
        checkNotDoneExtension(result.getFhirResource().getModifierExtension().get(0));
        checkRecordedExtension(result.getFhirResource().getExtension().get(0));
        checkNegationRationaleTypeCodeableConcept(result.getFhirResource().getStatusReason());
    }

    default void createNoNegation(QdmDataElement qdmDataElement) {
        qdmDataElement.setDataElementCodes(List.of(createDataElementCode()));
        qdmDataElement.setRank("12"); // wont map creates message
        qdmDataElement.setPriority(new QdmCodeSystem()); // wont map creates message

        qdmDataElement.setReason(createReason());

        qdmDataElement.setResult(BooleanNode.getFalse()); // wont map creates message

        qdmDataElement.setRelevantPeriod(createRelevantPeriod());
        qdmDataElement.setRelevantDatetime(createRelevantDatetime());
        qdmDataElement.setIncisionDatetime(createIncisionDatetime());
        qdmDataElement.setPerformer(createPerformer());

        qdmDataElement.setAnatomicalLocationSite(createAnatomicalLocationSite());
        qdmDataElement.setAuthorDatetime(createAuthorDatetime());
    }

    default void checkNoNegation(QdmToFhirConversionResult<Procedure> result) {
        checkBase(result.getFhirResource().getId(), result.getFhirResource().getSubject());

        assertEquals(Procedure.ProcedureStatus.COMPLETED, result.getFhirResource().getStatus());

        assertEquals(3, result.getConversionMessages().size());

        checkDataElementCodeableConcept(result.getFhirResource().getCode());

        assertEquals("Cannot convert QDM attribute rank", result.getConversionMessages().get(0));
        assertEquals("Cannot convert QDM attribute priority", result.getConversionMessages().get(1));

        checkReason(result.getFhirResource().getReasonCodeFirstRep());

        assertEquals("Cannot convert QDM attribute result", result.getConversionMessages().get(2));

        checkRelevantPeriod((Period) result.getFhirResource().getPerformed());
        checkIncisionDatetime(checkIncision(result.getFhirResource().getExtension()));

        checkAnatomicalLocationSite(result.getFhirResource().getBodySiteFirstRep());
    }

    default DateTimeType checkIncision(List<Extension> extensions) {
        assertEquals(2, extensions.size());
        Extension extension = extensions.get(0);

        assertThat(extension.getValue(), instanceOf(DateTimeType.class));

        return (DateTimeType) extension.getValue();
    }
}
