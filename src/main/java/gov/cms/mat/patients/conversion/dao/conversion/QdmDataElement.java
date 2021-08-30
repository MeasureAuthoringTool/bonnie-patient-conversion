package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.DateTimeType;

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

    private DateTimeType expiredDatetime;
    private DateTimeType activeDatetime;
    private DateTimeType birthDatetime;

    private DateTimeType authorDatetime;

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
    private DateTimeType resultDatetime;
    private QdmCodeSystem method;

    private QdmCodeSystem negationRationale;

    private QdmCodeSystem priority;

    private Participant participant;

    private LengthOfStay lengthOfStay;
    private QdmCodeSystem anatomicalLocationSite;
    private QdmCodeSystem severity;
    private QdmCodeSystem relationship;

    private DateTimeType incisionDatetime;

    private List<QdmComponent> components;

    private String description;

    private JsonNode targetOutcome;

    private String codeListId;

    private DateTimeType relevantDatetime;

    private String rank;

    private QdmCodeSystem category;
    private QdmCodeSystem medium;
    private DateTimeType sentDatetime;
    private DateTimeType receivedDatetime;

    private QdmPractitioner sender;
    private QdmPractitioner recipient;
    private QdmPractitioner prescriber;
    private QdmPractitioner dispenser;
    private QdmPractitioner performer;

    private Date statusDate;

    public String identifier() {
        return id;
    }
}
