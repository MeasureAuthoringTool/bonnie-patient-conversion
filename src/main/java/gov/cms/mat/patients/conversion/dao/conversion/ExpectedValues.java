package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectedValues {
    @JsonProperty("measure_id")
    private String measureId;
    @JsonProperty("population_index")
    private Integer populationIndex;

    @JsonProperty("STRAT")
    private Integer strat;

    @JsonProperty("IPP")
    private Integer initialPopulation;
    @JsonProperty("DENOM")
    private Integer denominator;
    @JsonProperty("DENEX")
    private Integer denominatorExclusions;
    @JsonProperty("NUMER")
    private Integer numerator;
    @JsonProperty("DENEXCEP")
    private Integer denominatorExceptions;

    @JsonProperty("NUMEX")
    private Integer numeratorExclusions;

    @JsonProperty("MSRPOPL")
    private Integer msrpopl;

    @JsonProperty("MSRPOPLEX")
    private Integer msrPoplex;

    @JsonProperty("OBSERV_UNIT")
    private String observeUnit;

    @JsonProperty("OBSERV")
    private JsonNode observ;
}
