package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AllergyIntoleranceConverter extends ConverterBase<AllergyIntolerance> {
    public static final String QDM_TYPE = "QDM::AllergyIntolerance";

    public AllergyIntoleranceConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<AllergyIntolerance> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.setPatient(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            allergyIntolerance.setCode(convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        allergyIntolerance.setId(qdmDataElement.getId());

        if (qdmDataElement.getPrevalencePeriod() != null) {
            if (qdmDataElement.getPrevalencePeriod().getLow() != null) {
                allergyIntolerance.setOnset(new DateTimeType(qdmDataElement.getPrevalencePeriod().getLow()));
            }

            if (qdmDataElement.getPrevalencePeriod().getHigh() != null) {
                allergyIntolerance.setLastOccurrence(qdmDataElement.getPrevalencePeriod().getHigh());
            }
        }

        allergyIntolerance.setRecordedDate(qdmDataElement.getAuthorDatetime());

        if (qdmDataElement.getType() != null) {
            conversionMessages.add("Cannot convert Allergy/Intolerance.type due to AllergyIntolerance.reaction tuple.");
        }

        if (qdmDataElement.getSeverity() != null) {
            conversionMessages.add("Cannot convert severity to the enum AllergyIntolerance.reaction.severity");
        }

        processNegation(qdmDataElement, allergyIntolerance);

        allergyIntolerance.setClinicalStatus(createCodeableConcept(
                "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical",
                "active",
                "Active"));

        return QdmToFhirConversionResult.<AllergyIntolerance>builder()
                .fhirResource(allergyIntolerance)
                .conversionMessages(conversionMessages)
                .build();

    }
}
