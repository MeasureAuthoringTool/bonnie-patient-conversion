package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QdmDataElement {
    @JsonProperty("_id")
    private String id;

    private List<QdmCodeSystem> dataElementCodes;

    @JsonProperty("_type")
    private String qdmType;

    private QdmCodeSystem type; //in there  "_id": "5c95406eb8484612c37f1f57",

    private String facilityLocation;

    private List<FacilityLocation> facilityLocations;
    private String qdmTitle;
    private String hqmfOid;
    private String qrdaOid;
    private String qdmCategory;
    private String qdmStatus;
    private String qdmVersion;

    private QdmPeriod participationPeriod;

    private List<String> relatedTo;

    private Date expiredDatetime;
    private Date activeDatetime;
    private Date birthDatetime;

    private Date authorDatetime;

    private Integer refills;
    private QdmQuantity dosage;
    private QdmQuantity supply;
    private QdmCodeSystem frequency;
    private Integer daysSupplied;
    private QdmCodeSystem setting;
    private QdmCodeSystem route;

    private QdmCodeSystem admissionSource;

    private QdmPeriod relevantPeriod;

    private QdmPeriod prevalencePeriod;

    private QdmCodeSystem dischargeDisposition;

    private List<Diagnoses> diagnoses;

    private QdmCodeSystem reason;

    private JsonNode result;

    private QdmReferenceRange referenceRange;

    private QdmCodeSystem status;
    private Date resultDatetime;
    private QdmCodeSystem method;

    private QdmCodeSystem negationRationale;

    private QdmCodeSystem priority;

    private Participant participant;

    private LengthOfStay lengthOfStay;
    private QdmCodeSystem anatomicalLocationSite;
    private QdmCodeSystem severity;
    private QdmCodeSystem relationship;

    private Date incisionDatetime;

    private List<QdmComponent> components;

    private String description;

    private TargetOutcome targetOutcome;

    private String codeListId;

    private Date relevantDatetime;

    private String rank;

    private QdmCodeSystem category;
    private QdmCodeSystem medium;
    private Date sentDatetime;
    private Date receivedDatetime;

    private QdmPractitioner sender;
    private QdmPractitioner recipient;
    private QdmPractitioner prescriber;
    private QdmPractitioner dispenser;
    private QdmPractitioner performer;

    public String identifier() {
        return id;
    }
}
