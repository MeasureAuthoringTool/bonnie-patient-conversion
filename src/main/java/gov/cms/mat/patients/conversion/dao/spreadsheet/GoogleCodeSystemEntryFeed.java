package gov.cms.mat.patients.conversion.dao.spreadsheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleCodeSystemEntryFeed {
    List<GoogleCodeSystemEntry> entry;
}
