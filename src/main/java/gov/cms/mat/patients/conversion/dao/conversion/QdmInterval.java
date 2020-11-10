package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class QdmInterval {
    Date low;
    Date high;
    Boolean lowClosed;
    Boolean highClosed;

    @JsonProperty("_type")
    String type;
}
