package gov.cms.mat.patients.conversion.service;


import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleConversionDataCodeSystemEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Data Source -> https://docs.google.com/spreadsheets/d/1POrP7vIKANlmY1IiUQZf4oXK2dwtgMHAz7vQJeLYMnw/edit?ts=5fada128#gid=0
 * <p>
 * FHir Namingsystem-terminologies -> https://www.hl7.org/fhir/namingsystem-terminologies.html
 * <p>
 * CD2-codes  https://terminology.hl7.org/1.0.0/CodeSystem-CD2.json.html
 */
@Service
@Slf4j
@Profile("!test")
public class MappingDataService implements GoogleDataService {
    private static final String LOG_MESSAGE = "Received {} records from the spreadsheet's JSON, URL: {}";
    private final RestTemplate restTemplate;

    @Value("${json.data.code-system-entry-url}")
    private String codeSystemEntryUrl;

    public MappingDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("codeSystemEntries")
    @Override
    public List<CodeSystemEntry> getCodeSystemEntries() {
        GoogleConversionDataCodeSystemEntry data = restTemplate.getForObject(codeSystemEntryUrl, GoogleConversionDataCodeSystemEntry.class);

        if (data != null && data.getFeed() != null && data.getFeed().getEntry() != null) {
            log.info(LOG_MESSAGE, data.getFeed().getEntry().size(), codeSystemEntryUrl);
            return convertGoogleDataToDao(data.getFeed().getEntry());
        } else {
            return Collections.emptyList();
        }
    }
}
