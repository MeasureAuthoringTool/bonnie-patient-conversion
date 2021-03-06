package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.QdmPractitioner;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import gov.cms.mat.patients.conversion.service.CodeSystemEntriesService;
import gov.cms.mat.patients.conversion.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PractitionerConverter extends ConverterBase<Practitioner> {
    private static final String UNITED_STATES_NATIONAL_PROVIDER_IDENTIFIER = "http://hl7.org/fhir/sid/us-npi";

    public PractitionerConverter(CodeSystemEntriesService codeSystemEntriesService,
                                 FhirContext fhirContext,
                                 ObjectMapper objectMapper,
                                 ValidationService validationService) {
        super(codeSystemEntriesService, fhirContext, objectMapper, validationService);
    }

    @Override
    public List<FhirDataElement> createDataElements(BonniePatient bonniePatient, Patient fhirPatient) {
        if (bonniePatient.getQdmPatient() == null || CollectionUtils.isEmpty(bonniePatient.getQdmPatient().getDataElements())) {
            return Collections.emptyList();
        } else {
            var dataElements = Collections.synchronizedList(new ArrayList<FhirDataElement>());

            bonniePatient.getQdmPatient().getDataElements()
                    .parallelStream()
                    .forEach(dataElement -> createPractitioners(dataElement, dataElements));

            return dataElements;
        }
    }

    public void createPractitioners(QdmDataElement dataElement, List<FhirDataElement> dataElements) {
        if (dataElement.getSender() != null) {
            dataElements.add(convertToFhirPractitioner(dataElement.getSender(), dataElement));
        }

        if (dataElement.getRecipient() != null) {
            dataElements.add(convertToFhirPractitioner(dataElement.getRecipient(), dataElement));
        }

        if (dataElement.getDispenser() != null) {
            dataElements.add(convertToFhirPractitioner(dataElement.getDispenser(), dataElement));
        }

        if (dataElement.getPerformer() != null) {
            dataElements.add(convertToFhirPractitioner(dataElement.getPerformer(), dataElement));
        }

        if (dataElement.getPrescriber() != null) {
            dataElements.add(convertToFhirPractitioner(dataElement.getPrescriber(), dataElement));
        }
    }

    @Override
    public String getQdmType() {
        return null;
    }

    @Override
    public QdmToFhirConversionResult<Practitioner> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public FhirDataElement convertToFhirPractitioner(QdmPractitioner qdmPractitioner, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        var practitioner = new Practitioner();
        practitioner.setId(qdmPractitioner.getId());

        if (qdmPractitioner.getRole() != null) {
            createFhirQualification(qdmPractitioner.getRole(), practitioner);
        }

        if (qdmPractitioner.getSpecialty() != null) {
            createFhirQualification(qdmPractitioner.getSpecialty(), practitioner);
        }

        if (qdmPractitioner.getQualification() != null) {
            createFhirQualification(qdmPractitioner.getQualification(), practitioner);
        }

        if (qdmPractitioner.getIdentifier() != null) {
            //https://www.hl7.org/fhir/identifier-registry.html
            practitioner.getIdentifierFirstRep()
                    .setValue(qdmPractitioner.getIdentifier().getValue())
                    .setSystem(UNITED_STATES_NATIONAL_PROVIDER_IDENTIFIER);
        }

        var conversionResult = QdmToFhirConversionResult.<Practitioner>builder()
                .fhirResource(practitioner)
                .conversionMessages(conversionMessages)
                .build();

        return validate(qdmDataElement, conversionResult);
    }

    private void createFhirQualification(QdmCodeSystem qdmCodeSystem, Practitioner practitioner) {
        var qualificationComponent = new Practitioner.PractitionerQualificationComponent();
        qualificationComponent.setCode(convertToCodeableConcept(qdmCodeSystem));
        practitioner.getQualification().add(qualificationComponent);
    }
}
