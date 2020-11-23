package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LengthOfStay {
    private Integer value;
    private String unit;
    @JsonProperty("_type")
    private String type;
}
