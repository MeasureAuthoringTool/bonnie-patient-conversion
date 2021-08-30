package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PatientCharacteristicPayerConverter extends ConverterBase<Coverage> {
    public static final String QDM_TYPE = "QDM::PatientCharacteristicPayer";

    public PatientCharacteristicPayerConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<Coverage> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var coverage = new Coverage();
        coverage.setBeneficiary(createPatientReference(fhirPatient));

        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            conversionMessages.add("Payer.code attribute not mapped");
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            convertRelevantPeriod(coverage, qdmDataElement);
        }

        coverage.setId(qdmDataElement.getId());

        return QdmToFhirConversionResult.<Coverage>builder()
                .fhirResource(coverage)
                .conversionMessages(conversionMessages)
                .build();
    }

    private void convertRelevantPeriod(Coverage coverage, QdmDataElement qdmDataElement) {
        var qdmPeriod = qdmDataElement.getRelevantPeriod();
        if (qdmPeriod.getLow() != null)
            qdmPeriod.getLow().setPrecision(TemporalPrecisionEnum.MILLI);
        if (qdmPeriod.getHigh() != null)
            qdmPeriod.getHigh().setPrecision(TemporalPrecisionEnum.MILLI);
        coverage.getPeriod().setStartElement(qdmPeriod.getLow());
        coverage.getPeriod().setEndElement(qdmPeriod.getHigh());
    }
}
