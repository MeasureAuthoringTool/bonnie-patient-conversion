package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.DateTimeType;

@Data
@NoArgsConstructor
public class QdmPeriod {
    private DateTimeType low;
    private DateTimeType high;
    private Boolean lowClosed;
    private Boolean highClosed;

    @JsonProperty("_type")
    private String type;
}
