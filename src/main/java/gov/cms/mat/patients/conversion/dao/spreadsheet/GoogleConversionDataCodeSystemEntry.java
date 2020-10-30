package gov.cms.mat.patients.conversion.dao.spreadsheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleConversionDataCodeSystemEntry {
    GoogleCodeSystemEntryFeed feed;
}
