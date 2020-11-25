package gov.cms.mat.patients.conversion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.mat.patients.conversion.dao.spreadsheet.CodeSystemEntry;
import gov.cms.mat.patients.conversion.dao.spreadsheet.GoogleConversionDataCodeSystemEntry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Service
@Slf4j
@Profile("test")
public class FileDataService implements GoogleDataService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    @Cacheable("codeSystemEntries")
    public List<CodeSystemEntry> getCodeSystemEntries() {
        String fromResource = getStringFromResource();
        GoogleConversionDataCodeSystemEntry data = objectMapper.readValue(fromResource, GoogleConversionDataCodeSystemEntry.class);

        log.info("Loaded google data from file");

        return convertGoogleDataToDao(data.getFeed().getEntry());
    }

    @SneakyThrows
    private String getStringFromResource() {
        File inputXmlFile = new File(this.getClass().getResource("/google/feed_data.json").getFile());
        return new String(Files.readAllBytes(inputXmlFile.toPath()));
    }
}
