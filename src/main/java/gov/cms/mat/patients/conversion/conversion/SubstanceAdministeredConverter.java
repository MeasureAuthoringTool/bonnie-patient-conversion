package gov.cms.mat.patients.conversion.conversion;


import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.helpers.SubstanceConverter;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SubstanceAdministeredConverter extends ConverterBase<NutritionOrder> implements SubstanceConverter {
    public static final String QDM_TYPE = "QDM::SubstanceAdministered";

    public SubstanceAdministeredConverter(CodeSystemEntriesService codeSystemEntriesService,
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
    public QdmToFhirConversionResult<NutritionOrder> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        return convertToFhirNutritionOrder(fhirPatient, qdmDataElement, this);
    }
}
