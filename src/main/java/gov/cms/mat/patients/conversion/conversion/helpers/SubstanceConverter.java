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

public interface SubstanceConverter extends DataElementFinder, FhirCreator {
    default QdmToFhirConversionResult<NutritionOrder> convertToFhirNutritionOrder(Patient fhirPatient,
                                                                                  QdmDataElement qdmDataElement,
                                                                                  ConverterBase<NutritionOrder> converterBase) {
        List<String> conversionMessages = new ArrayList<>();

        NutritionOrder nutritionOrder = new NutritionOrder();
        nutritionOrder.setPatient(createReference(fhirPatient));

        nutritionOrder.setIntent(NutritionOrder.NutritiionOrderIntent.NULL); // todo NO intent for SubstanceAdministered todo find

        //http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#821-substance
        //Constrain to Active, on-hold, Completed
        nutritionOrder.setStatus(NutritionOrder.NutritionOrderStatus.UNKNOWN); // Constrain to Active, on-hold, Completed
        conversionMessages.add(NO_STATUS_MAPPING);

        nutritionOrder.getOralDiet().addType(convertToCodeSystems(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));

        nutritionOrder.setId(qdmDataElement.get_id());

        if (qdmDataElement.getFrequency() != null) {
            // No Data
        }

        nutritionOrder.setDateTime(qdmDataElement.getAuthorDatetime());

        if (qdmDataElement.getRoute() != null) {
            // No Data
        }

        if (qdmDataElement.getDataElementCodes() != null) {
            nutritionOrder.getEnteralFormula()
                    .setBaseFormulaType(convertToCodeSystems(converterBase.getCodeSystemEntriesService(), qdmDataElement.getDataElementCodes()));
        }

        if (qdmDataElement.getDosage() != null) {

            nutritionOrder.getEnteralFormula().getAdministrationFirstRep()
                    .setQuantity(convertQuantity(qdmDataElement.getDosage()));
        }


        if (qdmDataElement.getRelevantPeriod() != null) {
            // todo stan how to fully convert
            Timing timing = new Timing();
            timing.setEvent(List.of(new DateTimeType(qdmDataElement.getRelevantPeriod().getLow())));
            nutritionOrder.getEnteralFormula().getAdministrationFirstRep().setSchedule(timing);
            //conversionMessages.add("Unable to convert RelevantPeriod to a Fhir Timing object");
        }

        if (qdmDataElement.getRefills() != null) {
            System.out.println("HI");
        }



        return QdmToFhirConversionResult.<NutritionOrder>builder()
                .fhirResource(nutritionOrder)
                .conversionMessages(conversionMessages)
                .build();


    }
}
