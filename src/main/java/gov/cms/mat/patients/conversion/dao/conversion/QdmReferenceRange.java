package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QdmReferenceRange {
    private QdmQuantity low;
    private QdmQuantity high;
    private Boolean lowClosed;
    private Boolean highClosed;

    @JsonProperty("_type")
    private String type;
}
