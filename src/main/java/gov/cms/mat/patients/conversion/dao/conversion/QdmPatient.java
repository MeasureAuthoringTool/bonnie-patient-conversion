package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
public class QdmPatient {
    @JsonProperty("_id")
    String id;

    String qdmVersion;

    Date birthDatetime;
    ExtendedData extendedData;

    List<QdmDataElement> dataElements;
}
