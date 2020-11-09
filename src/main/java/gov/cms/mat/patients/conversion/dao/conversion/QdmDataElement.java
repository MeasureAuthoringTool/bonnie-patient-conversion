package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QdmDataElement {
    String _id;
    List<QdmCodeSystem> dataElementCodes;

    String _type;

    QdmCodeSystem type; //in there  "_id": "5c95406eb8484612c37f1f57",

    String facilityLocation;

    List<FacilityLocation> facilityLocations;
    String qdmTitle;
    String hqmfOid;
    String qrdaOid;
    String qdmCategory;
    String qdmStatus;
    String qdmVersion;

    QdmPeriod participationPeriod;

    // ids of other patients??
    /* "relatedTo": [
          "5c7592f1b8484660416e290c"
        ] */
    List<String> relatedTo;

    Date expiredDatetime;
    Date activeDatetime;
    Date birthDatetime;

    Date authorDatetime;


    Integer refills;
    QdmQuantity dosage;
    QdmQuantity supply;
    QdmCodeSystem frequency;
    Integer daysSupplied;
    QdmCodeSystem setting;
    QdmCodeSystem route;

    QdmCodeSystem admissionSource;

    QdmPeriod relevantPeriod;

    QdmPeriod prevalencePeriod;

    QdmCodeSystem dischargeDisposition;

    List<Diagnoses> diagnoses;

    QdmCodeSystem reason;

    JsonNode result;

    QdmReferenceRange referenceRange;

    QdmCodeSystem status;
    Date resultDatetime;
    QdmCodeSystem method;

    QdmCodeSystem negationRationale;

    QdmCodeSystem priority;

    Participant participant;

    LengthOfStay lengthOfStay;
    QdmCodeSystem anatomicalLocationSite;
    QdmCodeSystem severity;
    QdmCodeSystem relationship;

    Date incisionDatetime;

    List<QdmComponent> components;

    String description;

    TargetOutcome targetOutcome;

    String codeListId;

    Date relevantDatetime;

    String rank;

    QdmCodeSystem category;
    QdmCodeSystem medium;
    Date sentDatetime;
    Date receivedDatetime;

    QdmPractitioner sender;
    QdmPractitioner recipient;
    QdmPractitioner prescriber;
    QdmPractitioner dispenser;
    QdmPractitioner performer;

    public String identifier() {
        return _id;
    }
}
