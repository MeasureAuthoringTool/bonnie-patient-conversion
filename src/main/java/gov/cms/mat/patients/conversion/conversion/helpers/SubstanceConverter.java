package gov.cms.mat.patients.conversion.conversion.helpers;

import gov.cms.mat.patients.conversion.conversion.ConverterBase;
import gov.cms.mat.patients.conversion.conversion.results.QdmToFhirConversionResult;
import gov.cms.mat.patients.conversion.dao.conversion.QdmDataElement;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Timing;

import java.util.ArrayList;
import java.util.List;

import static gov.cms.mat.patients.conversion.conversion.ConverterBase.NO_STATUS_MAPPING;
import static gov.cms.mat.patients.conversion.conversion.ConverterBase.UNEXPECTED_DATA_LOG_MESSAGE;

public interface SubstanceConverter extends DataElementFinder, FhirCreator {
    default QdmToFhirConversionResult<NutritionOrder> convertToFhirNutritionOrder(Patient fhirPatient,
                                                                                  QdmDataElement qdmDataElement,
                                                                                  ConverterBase<NutritionOrder> converterBase) {
        List<String> conversionMessages = new ArrayList<>();

        NutritionOrder nutritionOrder = new NutritionOrder();
        nutritionOrder.setPatient(createPatientReference(fhirPatient));

        nutritionOrder.setIntent(NutritionOrder.NutritiionOrderIntent.NULL); // todo NO intent for SubstanceAdministered todo find

        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#821-substance
        //Constrain to Active, on-hold, Completed
        nutritionOrder.setStatus(NutritionOrder.NutritionOrderStatus.UNKNOWN); // Constrain to Active, on-hold, Completed
        conversionMessages.add(NO_STATUS_MAPPING);

        nutritionOrder.getOralDiet().addType(converterBase.convertToCodeableConcept(qdmDataElement.getDataElementCodes()));

        nutritionOrder.setId(qdmDataElement.getId());

        if (qdmDataElement.getFrequency() != null) {
            converterBase.getLog().info(UNEXPECTED_DATA_LOG_MESSAGE, converterBase.getQdmType(), "frequency");
        }

        nutritionOrder.setDateTime(qdmDataElement.getAuthorDatetime());

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
            Timing timing = new Timing();
            timing.setEvent(List.of(new DateTimeType(qdmDataElement.getRelevantPeriod().getLow())));
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
