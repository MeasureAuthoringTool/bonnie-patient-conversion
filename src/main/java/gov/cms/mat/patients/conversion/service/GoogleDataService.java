package gov.cms.mat.patients.conversion.service;

import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleCodeSystemEntry;

import java.util.List;
import java.util.stream.Collectors;

import static gov.cms.mat.patients.conversion.dao.spreadsheet.SpreadSheetUtils.getData;

public interface GoogleDataService {
    List<CodeSystemEntry> getCodeSystemEntries();

    default List<CodeSystemEntry> convertGoogleDataToDao(List<GoogleCodeSystemEntry> entry) {
        return entry.stream()
                .map(e -> {
                    var r = new CodeSystemEntry();
                    r.setOid(getData(e.getOid()));
                    r.setUrl(getData(e.getUrl()));
                    r.setName(getData(e.getName()));
                    r.setDefaultVsacVersion(getData(e.getDefaultVsacVersion()));
                    return r;

                }).sorted()
                .collect(Collectors.toList());
    }
}
