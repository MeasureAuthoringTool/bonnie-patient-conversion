package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmReferenceRange {
    QdmQuantity low;
    QdmQuantity high;
    Boolean lowClosed;
    Boolean highClosed;

    @JsonProperty("_type")
    String type;
}
