package gov.cms.mat.patients.conversion.service;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.PatientConverter;
import gov.cms.mat.patients.conversion.conversion.PractitionerConverter;
import gov.cms.mat.patients.conversion.conversion.helpers.FhirCreator;
import gov.cms.mat.patients.conversion.dao.conversion.BonniePatient;
import gov.cms.mat.patients.conversion.dao.results.ConversionResult;
import gov.cms.mat.patients.conversion.dao.results.ConvertedPatient;
import gov.cms.mat.patients.conversion.dao.results.FhirDataElement;
import gov.cms.mat.patients.conversion.exceptions.PatientConversionException;
import gov.cms.mat.patients.conversion.service.helpers.RelatedToProcessor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatientConversionService implements FhirCreator {
    private static final int THREAD_POOL_TIMEOUT_MINUTES = 2;

    private final PatientConverter patientConverter;
    private final PractitionerConverter practitionerConverter;
    private final ObjectMapper objectMapper;
    private final FhirContext fhirContext;

    //Loads all beans that extends Abstract class ConverterBase except PractitionerConverter
    private final List<ConverterBase<? extends IBaseResource>> converters;

    public PatientConversionService(PatientConverter patientConverter,
                                    ObjectMapper objectMapper,
                                    FhirContext fhirContext,
                                    Map<String, ConverterBase<? extends IBaseResource>> converterMap) {
        this.patientConverter = patientConverter;
        this.fhirContext = fhirContext;
        this.objectMapper = objectMapper;

        this.practitionerConverter = (PractitionerConverter) converterMap.get("practitionerConverter");

        this.converters = converterMap.entrySet().stream()
                .filter(x -> !x.getKey().equals("practitionerConverter"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<ConversionResult> processMany(List<BonniePatient> bonniePatients) {
        List<ConversionResult> results = bonniePatients.parallelStream()
                .map(this::processOne)
                .collect(Collectors.toList());

        //  maintain the order from the input list
        return bonniePatients.stream()
                .map(b -> findResultById(b.getId(), results))
                .collect(Collectors.toList());
    }

    public ConversionResult processOne(BonniePatient bonniePatient) {
        try {
            List<CompletableFuture<List<FhirDataElement>>> futures = new ArrayList<>();

            Set<String> qdmTypes = collectQdmTypes(bonniePatient);

            var qdmToFhirPatientResult = patientConverter.convert(bonniePatient, qdmTypes);
            var fhirPatient = qdmToFhirPatientResult.getFhirPatient();

            // creates Practitioners, if they exist,  that wil be used later for references
            processFuture(bonniePatient, fhirPatient, practitionerConverter, futures);

            converters.forEach(converter -> {
                if (qdmTypes.contains(converter.getQdmType())) {
                    processFuture(bonniePatient, fhirPatient, converter, futures);
                }
            });

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

            List<FhirDataElement> fhirDataElements = findFhirDataElementsFromFutures(futures);

            processRelatedTo(fhirDataElements);

            var convertedPatient = ConvertedPatient.builder()
                    .fhirPatient(objectMapper.readTree(toJson(fhirContext, fhirPatient)))
                    .outcome(qdmToFhirPatientResult.getOutcome())
                    .build();

            return buildResult(bonniePatient, fhirDataElements, convertedPatient);

        } catch (InterruptedException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            throw new PatientConversionException("oops", ie);
        } catch (Exception e) {
            log.warn("Error ", e);
            throw new PatientConversionException("Error:" + e.getMessage());
        }
    }

    private ConversionResult buildResult(BonniePatient bonniePatient,
                                         List<FhirDataElement> fhirDataElements,
                                         ConvertedPatient convertedPatient) {
        var timeStamp = Instant.now();
        return ConversionResult.builder()
                .id(bonniePatient.getId())
                .expectedValues(bonniePatient.getExpectedValues())
                .measureIds(bonniePatient.getMeasureIds())
                .fhirPatient(convertedPatient.getFhirPatient())
                .patientOutcome(convertedPatient.getOutcome())
                .dataElements(fhirDataElements)
                .createdAt(timeStamp)
                .updatedAt(timeStamp)
                .build();
    }


    private List<FhirDataElement> findFhirDataElementsFromFutures(List<CompletableFuture<List<FhirDataElement>>> futures) {
        return futures.stream()
                .map(this::findDataFromFuture)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<FhirDataElement> findDataFromFuture(CompletableFuture<List<FhirDataElement>> f) {
        try {
            return f.get();
        } catch (InterruptedException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            throw new PatientConversionException("InterruptedException from future", ie);
        } catch (Exception e) {
            log.error("Error with future get", e);
            return Collections.emptyList();
        }
    }


    private void processFuture(BonniePatient bonniePatient,
                               Patient fhirPatient,
                               ConverterBase<? extends IBaseResource> converter,
                               List<CompletableFuture<List<FhirDataElement>>> futures) {
        CompletableFuture<List<FhirDataElement>> future = converter.convertToFhirDataElement(bonniePatient, fhirPatient);
        future.orTimeout(THREAD_POOL_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        futures.add(future);
    }

    private ConversionResult findResultById(String id, List<ConversionResult> results) {
        return results.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No result found with the id: " + id));
    }

    private void processRelatedTo(List<FhirDataElement> fhirDataElements) {
        var relatedToProcessor = new RelatedToProcessor(fhirContext, objectMapper, fhirDataElements);
        relatedToProcessor.process();
    }
}
