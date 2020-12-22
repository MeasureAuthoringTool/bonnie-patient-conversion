package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.INCISION_DATE_TIME_URL;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UNEXPECTED_DATA_LOG_MESSAGE;

public interface ProcedureConverter extends DataElementFinder, FhirCreator {

    default QdmToFhirConversionResult<Procedure> convertToFhirProcedure(Patient fhirPatient,
                                                                        QdmDataElement qdmDataElement,
                                                                        ConverterBase<Procedure> converterBase) {
        List<String> conversionMessages = new ArrayList<>();
        Procedure procedure = new Procedure();
        procedure.setSubject(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            procedure.setCode(converterBase.convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        procedure.setId(qdmDataElement.getId());

        if (qdmDataElement.getRank() != null) {
            conversionMessages.add("Cannot convert QDM attribute rank");
        }

        if (qdmDataElement.getPriority() != null) {
            conversionMessages.add("Cannot convert QDM attribute priority");
        }

        if (qdmDataElement.getReason() != null) {
            procedure.getReasonCode().add(converterBase.convertToCodeableConcept(qdmDataElement.getReason()));
        }

        if (qdmDataElement.getResult() != null) {
            conversionMessages.add("Cannot convert QDM attribute result");
        }

        if (!converterBase.processNegation(qdmDataElement, procedure)) {
            // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8152-intervention-performed
            // constrain to “completed”
            procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
        }

        if (qdmDataElement.getAnatomicalLocationSite() != null) {
            CodeableConcept codeableConcept = converterBase.convertToCodeableConcept(qdmDataElement.getAnatomicalLocationSite());
            procedure.setBodySite(List.of(codeableConcept));
        }

        // Relevant getRelevantPeriod() and getRelevantDatetime() bot map to fhir as setPerformed(Type value)
        // since period has more data we can let that win if we have both
        boolean havePeriod = false;

        if (qdmDataElement.getRelevantPeriod() != null) {
            procedure.setPerformed(convertPeriod(qdmDataElement.getRelevantPeriod()));
            havePeriod = true;
        }

        if (!havePeriod && qdmDataElement.getRelevantDatetime() != null) {
            procedure.setPerformed(new DateTimeType(qdmDataElement.getRelevantDatetime()));
        }

        if (qdmDataElement.getIncisionDatetime() != null) {
            procedure.getExtension()
                    .add(new Extension(INCISION_DATE_TIME_URL));
            Extension extension = procedure.getExtension().get(0);
            extension.setValue(new DateTimeType(qdmDataElement.getIncisionDatetime()));
        }

        if (CollectionUtils.isNotEmpty(qdmDataElement.getComponents())) {
            conversionMessages.add("Cannot convert QDM attribute components");
        }

        if (qdmDataElement.getPerformer() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "performer");
            procedure.getPerformerFirstRep().setActor(createPractitionerReference(qdmDataElement.getPerformer()));
        }

        return QdmToFhirConversionResult.<Procedure>builder()
                .fhirResource(procedure)
                .conversionMessages(conversionMessages)
                .build();
    }
}
