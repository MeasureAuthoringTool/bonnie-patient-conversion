package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FamilyHistoryConverter extends ConverterBase<FamilyMemberHistory> {
    public static final String QDM_TYPE = "QDM::FamilyHistory";

    public FamilyHistoryConverter(CodeSystemEntriesService codeSystemEntriesService,
                                  FhirContext fhirContext,
                                  ObjectMapper objectMapper,
                                  ValidationService validationService) {
        super(codeSystemEntriesService, fhirContext, objectMapper, validationService);
    }

    @Override
    public String getQdmType() {
        return QDM_TYPE;
    }

    @Override
    public QdmToFhirConversionResult<FamilyMemberHistory> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        FamilyMemberHistory familyMemberHistory = new FamilyMemberHistory();
        familyMemberHistory.setPatient(createPatientReference(fhirPatient));

        FamilyMemberHistory.FamilyMemberHistoryConditionComponent familyMemberHistoryConditionComponent = familyMemberHistory.getConditionFirstRep();
        familyMemberHistoryConditionComponent.setCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));

        familyMemberHistory.setId(qdmDataElement.getId());

        familyMemberHistory.setDate(qdmDataElement.getAuthorDatetime());

        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#812-family-history
        //Constrain to Completed, entered-in-error, not-done
        familyMemberHistory.setStatus(FamilyMemberHistory.FamilyHistoryStatus.NULL);
        conversionMessages.add(NO_STATUS_MAPPING);

        FamilyMemberHistory.FamilyMemberHistoryConditionComponent  familyMemberHistoryConditionComponent =familyMemberHistory.getConditionFirstRep();
        familyMemberHistoryConditionComponent.setCode(convertToCodeSystems(getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));

        familyMemberHistory.setId(qdmDataElement.getId());

        familyMemberHistory.setDate(qdmDataElement.getAuthorDatetime());

        if (qdmDataElement.getRelationship() != null) {
            //https://terminology.hl7.org/1.0.0/CodeSystem-v3-RoleCode.html
            // we only have AUNT as an example, we could convert if knew the input set and no system
            if (qdmDataElement.getRelationship().getSystem() == null) {
                conversionMessages.add("RelationShip for code " + qdmDataElement.getRelationship().getCode() + " has no system");
            } else {
                familyMemberHistory.setRelationship(convertToCodeableConcept(qdmDataElement.getRelationship()));
            }
        }

        processNegation(qdmDataElement, familyMemberHistory); // should never have any

        return QdmToFhirConversionResult.<FamilyMemberHistory>builder()
                .fhirResource(familyMemberHistory)
                .conversionMessages(conversionMessages).build();
    }
}
