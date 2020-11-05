package gov.cms.mat.patients.conversion.conversion;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.conversion.Prescriber;
import gov.cms.mat.patients.conversion.dao.conversion.QdmCodeSystem;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import gov.cms.mat.patients.conversion.dao.conversion.Sender;
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
import java.util.stream.Collectors;

@Slf4j
@Component
public class PractitionerConverter extends ConverterBase<Practitioner> {
  private static final String  UNITED_STATES_NATIONAL_PROVIDER_IDENTIFIER = "http://hl7.org/fhir/sid/us-npi";


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
            var senderList = bonniePatient.getQdmPatient().getDataElements().stream()
                    .filter(d -> d.getSender() != null)
                    .map(d -> convertToFhirPractitioner(d.getSender(), d))
                    .collect(Collectors.toList());

            var recipientList = bonniePatient.getQdmPatient().getDataElements().stream()
                    .filter(d -> d.getRecipient() != null)
                    .map(d -> convertToFhirPractitioner(d.getRecipient(), d))
                    .collect(Collectors.toList());

            bonniePatient.getQdmPatient().getDataElements().stream()
                    .filter(d -> d.getDispenser() != null)
                    .map(d -> convertToFhirTest(d.getDispenser(), d));

            bonniePatient.getQdmPatient().getDataElements().stream()
                    .filter(d -> d.getPerformer() != null)
                    .map(d -> convertToFhirTest(d.getPerformer(), d));


            var combinedList = new ArrayList<FhirDataElement>();
            combinedList.addAll(senderList);
            combinedList.addAll(recipientList);
            return combinedList;
        }
    }

    private Object convertToFhirTest(Prescriber dispenser, QdmDataElement d) {

        return null;
    }

    @Override
    public String getQdmType() {
        return null;
    }

    @Override
    public QdmToFhirConversionResult<Practitioner> convertToFhir(Patient fhirPatient, QdmDataElement qdmDataElement) {
        throw new UnsupportedOperationException("Not implemented");
    }


    public FhirDataElement convertToFhirPractitioner(Sender sender, QdmDataElement qdmDataElement) {
        List<String> conversionMessages = new ArrayList<>();

        Practitioner practitioner = new Practitioner();
        practitioner.setId(sender.get_id());

        if (sender.getRole() != null) {
            creeateFhirQualification(sender.getRole(), practitioner);
        }

        if (sender.getSpecialty() != null) {
            creeateFhirQualification(sender.getSpecialty(), practitioner);
        }

        if (sender.getQualification() != null) {
            creeateFhirQualification(sender.getQualification(), practitioner);
        }

        if( sender.getIdentifier() != null) {
            practitioner.getIdentifierFirstRep()
                    .setValue(sender.getIdentifier().getValue())
                    .setSystem(UNITED_STATES_NATIONAL_PROVIDER_IDENTIFIER);
        }

        var conversionResult = QdmToFhirConversionResult.<Practitioner>builder()
                .fhirResource(practitioner)
                .conversionMessages(conversionMessages)
                .build();

        return validate(qdmDataElement, conversionResult);
    }

    private void creeateFhirQualification(QdmCodeSystem qdmCodeSystem, Practitioner practitioner) {
        var qualificationComponent = new Practitioner.PractitionerQualificationComponent();
        qualificationComponent.setCode(convertToCodeableConcept(codeSystemEntriesService, qdmCodeSystem));
        practitioner.getQualification().add(qualificationComponent);
    }
}
