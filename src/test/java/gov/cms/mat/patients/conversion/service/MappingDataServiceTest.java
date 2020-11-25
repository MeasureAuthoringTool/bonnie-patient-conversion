package gov.cms.mat.patients.conversion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.ResourceFileUtil;
import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleCodeSystemEntryFeed;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleConversionDataCodeSystemEntry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MappingDataServiceTest implements ResourceFileUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private MappingDataService mappingDataService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mappingDataService, "codeSystemEntryUrl", "http://howdy.doody.com");
    }

    @SneakyThrows
    @Test
    void getCodeSystemEntries() {
        String json = getStringFromResource("/google/feed_data.json");
        GoogleConversionDataCodeSystemEntry data = objectMapper.readValue(json, GoogleConversionDataCodeSystemEntry.class);

        when(restTemplate.getForObject("http://howdy.doody.com", GoogleConversionDataCodeSystemEntry.class)).thenReturn(data);

        List<CodeSystemEntry> codeSystemEntries = mappingDataService.getCodeSystemEntries();

        assertEquals(data.getFeed().getEntry().size(), codeSystemEntries.size());
    }

    @SneakyThrows
    @Test
    void getCodeSystemEntriesNoEntriesFound() {
        GoogleConversionDataCodeSystemEntry data = new GoogleConversionDataCodeSystemEntry();
        data.setFeed(new GoogleCodeSystemEntryFeed());

        when(restTemplate.getForObject("http://howdy.doody.com", GoogleConversionDataCodeSystemEntry.class)).thenReturn(data);

        List<CodeSystemEntry> codeSystemEntries = mappingDataService.getCodeSystemEntries();

        assertTrue(codeSystemEntries.isEmpty());
    }
}