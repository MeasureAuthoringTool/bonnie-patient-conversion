package gov.cms.mat.patients.conversion.dao.spreadsheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
@EqualsAndHashCode
public class GoogleCodeSystemEntry {
    @JsonProperty("gsx$oid")
    Cell oid;

    @JsonProperty("gsx$url")
    Cell url;

    @JsonProperty("gsx$codesystemname")
    Cell name;
}
