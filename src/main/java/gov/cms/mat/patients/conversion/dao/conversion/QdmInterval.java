package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class QdmInterval {
    private Date low;
    private Date high;
    private Boolean lowClosed;
    private Boolean highClosed;

    @JsonProperty("_type")
    private String type;
}
