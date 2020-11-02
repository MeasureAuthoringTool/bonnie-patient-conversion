package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;

import java.util.ArrayList;
import java.util.List;

public interface ProcedureConverter extends DataElementFinder, FhirCreator {

    default QdmToFhirConversionResult<Procedure> convertToFhirProcedure(Patient fhirPatient,
                                                                        QdmDataElement qdmDataElement,
                                                                        ConverterBase<Procedure> converterBase) {
        List<String> conversionMessages = new ArrayList<>();
        Procedure procedure = new Procedure();

        procedure.setId(qdmDataElement.get_id());
        procedure.setCode(convertToCodeSystems(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));
        procedure.setSubject(createReference(fhirPatient));

        //Todo qdmDataElement.getRank() is available, How can we map it to Encounter.extension.extension:rank.value[x]:valuePositiveInt

        if (qdmDataElement.getPriority() != null) {
            //If available, How do we map it to qicore-encounter-procedure?
        }

        if (qdmDataElement.getRelevantDatetime() != null) {
            //ask Michael on what is Type?
//            procedure.setPerformed(qdmDataElement.getRelevantDatetime());
        }

        if (qdmDataElement.getIncisionDatetime() != null) {
//           ask Michael. Should be mapped to Procedure.extension:incisionDateTime
        }

        procedure.setPerformed(convertPeriod(qdmDataElement.getRelevantPeriod()));
        /**
         * {
         * "dataTypeDescription": "Intervention, Performed",
         * "matAttributeName": "authorDatetime",
         * "fhirQicoreMapping": "ServiceRequest.authoredOn",
         * "fhirResource": "Procedure",
         * "fhirType": "dateTime",
         * "fhirElement": "authoredOn",
         * "helpWording": "Definition: When the request transitioned to being actionable.\n",
         * "dropDown": []
         * },
         */
        //todo stan
        // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8152-intervention-performed
        // procedure.setAuthoredOn todo how to map this see negataion


        if (qdmDataElement.getReason() != null) {
            procedure.setReasonCode(List.of(convertToCodeableConcept(converterBase.getCodeSystemEntriesService(), qdmDataElement.getReason())));
        }

        if (!converterBase.processNegation(qdmDataElement, procedure)) {
            // http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8152-intervention-performed
            // constrain to “completed”
            procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
        }

        return QdmToFhirConversionResult.<Procedure>builder()
                .fhirResource(procedure)
                .conversionMessages(conversionMessages)
                .build();
    }
}
