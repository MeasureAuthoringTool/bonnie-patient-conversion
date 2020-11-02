package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.INCISION_DATE_TIME_URL;

public interface ProcedureConverter extends DataElementFinder, FhirCreator {

    default QdmToFhirConversionResult<Procedure> convertToFhirProcedure(Patient fhirPatient,
                                                                        QdmDataElement qdmDataElement,
                                                                        ConverterBase<Procedure> converterBase) {
        List<String> conversionMessages = new ArrayList<>();
        Procedure procedure = new Procedure();
        procedure.setSubject(createReference(fhirPatient));

        procedure.setCode(convertToCodeSystems(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));
        procedure.setId(qdmDataElement.get_id());

//        if (qdmDataElement.getRank() != null) {
//            //todo stan
//        }

//        if (qdmDataElement.getPriority() != null) {
//            //todo stan
//        }

        if (qdmDataElement.getReason() != null) {
            procedure.setReasonCode(List.of(convertToCodeableConcept(converterBase.getCodeSystemEntriesService(), qdmDataElement.getReason())));
        }

//        if (qdmDataElement.getResult() != null) {
//            //todo stan
//        }

        if (!converterBase.processNegation(qdmDataElement, procedure)) {
            // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8152-intervention-performed
            // constrain to “completed”
            procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
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
            procedure.setExtension(List.of(new Extension(INCISION_DATE_TIME_URL)));
            Extension extension = procedure.getExtension().get(0);
            extension.setValue(new DateTimeType(qdmDataElement.getIncisionDatetime()));
        }

//        if (CollectionUtils.isNotEmpty(qdmDataElement.getComponents())) {
//            //todo stan
//        }

//        if (qdmDataElement.getPerformer() != null) {
//            // No data
//        }

        return QdmToFhirConversionResult.<Procedure>builder()
                .fhirResource(procedure)
                .conversionMessages(conversionMessages)
                .build();
    }
}
