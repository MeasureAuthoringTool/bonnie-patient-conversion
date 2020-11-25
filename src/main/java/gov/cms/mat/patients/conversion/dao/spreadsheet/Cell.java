package gov.cms.mat.patients.conversion.dao.spreadsheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cell {
    @JsonProperty("$t")
    String data;
}