package gov.cms.mat.patients.conversion.service;


import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleConversionDataCodeSystemEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static gov.cms.mat.patients.conversion.dao.spreadsheet.SpreadSheetUtils.getData;

@Service
@Slf4j
public class MappingDataService {
    private static final String LOG_MESSAGE = "Received {} records from the spreadsheet's JSON, URL: {}";
    private final RestTemplate restTemplate;


    @Value("${json.data.code-system-entry-url}")
    private String codeSystemEntryUrl;

    public MappingDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("codeSystemEntries")
    public List<CodeSystemEntry> getCodeSystemEntries() {
        GoogleConversionDataCodeSystemEntry data = restTemplate.getForObject(codeSystemEntryUrl, GoogleConversionDataCodeSystemEntry.class);

        if (data != null && data.getFeed() != null && data.getFeed().getEntry() != null) {
            log.info(LOG_MESSAGE, data.getFeed().getEntry().size(), codeSystemEntryUrl);

            return data.getFeed().getEntry().stream()
                    .map(e -> {
                        var r = new CodeSystemEntry();
                        r.setOid(getData(e.getOid()));
                        r.setUrl(getData(e.getUrl()));
                        r.setName(getData(e.getName()));
                        r.setDefaultVsacVersion(getData(e.getDefaultVsacVersion()));
                        return r;

                    }).sorted()
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
