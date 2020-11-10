package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpectedValues {
    @JsonProperty("measure_id")
    String measureId;
    @JsonProperty("population_index")
    Integer populationIndex;

    @JsonProperty("STRAT")
    Integer strat;

    @JsonProperty("IPP")
    Integer initialPopulation;
    @JsonProperty("DENOM")
    Integer denominator;
    @JsonProperty("DENEX")
    Integer denominatorExclusions;
    @JsonProperty("NUMER")
    Integer numerator;
    @JsonProperty("DENEXCEP")
    Integer denominatorExceptions;

    @JsonProperty("NUMEX")
    Integer numeratorExclusions;

    @JsonProperty("MSRPOPL")
    Integer msrpopl;

    @JsonProperty("MSRPOPLEX")
    Integer msrPoplex;

    @JsonProperty("OBSERV_UNIT")
    String observeUnit;

    @JsonProperty("OBSERV")
    JsonNode observ;
}
