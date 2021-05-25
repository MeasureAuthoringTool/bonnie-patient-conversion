package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QdmPatient {
    @JsonProperty("_id")
    private String id;

    private String qdmVersion;

    private Date birthDatetime;
    private ExtendedData extendedData;

    private List<QdmDataElement> dataElements;
}
