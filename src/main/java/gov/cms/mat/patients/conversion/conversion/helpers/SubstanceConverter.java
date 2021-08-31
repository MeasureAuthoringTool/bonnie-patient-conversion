package gov.cms.mat.patients.conversion.conversion.helpers;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Timing;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UNEXPECTED_DATA_LOG_MESSAGE;

public interface SubstanceConverter extends DataElementFinder, FhirCreator {
    default QdmToFhirConversionResult<NutritionOrder> convertToFhirNutritionOrder(Patient fhirPatient,
                                                                                  QdmDataElement qdmDataElement,
                                                                                  ConverterBase<NutritionOrder> converterBase) {
        List<String> conversionMessages = new ArrayList<>();

        var nutritionOrder = new NutritionOrder();
        nutritionOrder.setPatient(createPatientReference(fhirPatient));

        nutritionOrder.setIntent(NutritionOrder.NutritiionOrderIntent.NULL);


        nutritionOrder.setStatus(NutritionOrder.NutritionOrderStatus.COMPLETED); // Constrain to Active, on-hold, Completed


        if (CollectionUtils.isNotEmpty(qdmDataElement.getDataElementCodes())) {
            nutritionOrder.getOralDiet().addType(converterBase.convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        nutritionOrder.setId(qdmDataElement.getId());

        if (qdmDataElement.getFrequency() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "frequency");
        }

        if (qdmDataElement.getAuthorDatetime() != null) {
            qdmDataElement.getAuthorDatetime().setPrecision(TemporalPrecisionEnum.MILLI);
            nutritionOrder.setDateTimeElement(qdmDataElement.getAuthorDatetime());
        }

        if (qdmDataElement.getRoute() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "route");
        }

        if (qdmDataElement.getDataElementCodes() != null) {
            nutritionOrder.getEnteralFormula()
                    .setBaseFormulaType(converterBase.convertToCodeableConcept(qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getDosage() != null) {
            nutritionOrder.getEnteralFormula().getAdministrationFirstRep()
                    .setQuantity(convertQuantity(qdmDataElement.getDosage()));
        }

        if (qdmDataElement.getRelevantPeriod() != null) {
            var timing = new Timing();
            if (qdmDataElement.getRelevantPeriod().getLow() != null) {
                qdmDataElement.getRelevantPeriod().getLow().setPrecision(TemporalPrecisionEnum.MILLI);
                timing.getEvent().add(qdmDataElement.getRelevantPeriod().getLow());
            }
            nutritionOrder.getEnteralFormula().getAdministrationFirstRep().setSchedule(timing);
        }

        if (qdmDataElement.getRefills() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "refills");
        }

        return QdmToFhirConversionResult.<NutritionOrder>builder()
                .fhirResource(nutritionOrder)
                .conversionMessages(conversionMessages)
                .build();
    }
}
