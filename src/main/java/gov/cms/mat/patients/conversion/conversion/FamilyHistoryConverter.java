package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

        var familyMemberHistory = new FamilyMemberHistory();
        familyMemberHistory.setPatient(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            var familyMemberHistoryConditionComponent = familyMemberHistory.getConditionFirstRep();
            familyMemberHistoryConditionComponent.setCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        familyMemberHistory.setId(qdmDataElement.getId());

        familyMemberHistory.setDateElement(qdmDataElement.getAuthorDatetime());

        familyMemberHistory.setStatus(FamilyMemberHistory.FamilyHistoryStatus.COMPLETED);

        if (qdmDataElement.getRelationship() != null) {
            if (qdmDataElement.getRelationship().getSystem() == null) {
                qdmDataElement.getRelationship().setSystem("2.16.840.1.113883.5.111");
            }

            familyMemberHistory.setRelationship(convertToCodeableConcept(qdmDataElement.getRelationship()));
        }

        processNegation(qdmDataElement, familyMemberHistory); // should never have any

        return QdmToFhirConversionResult.<FamilyMemberHistory>builder()
                .fhirResource(familyMemberHistory)
                .conversionMessages(conversionMessages).build();
    }
}
