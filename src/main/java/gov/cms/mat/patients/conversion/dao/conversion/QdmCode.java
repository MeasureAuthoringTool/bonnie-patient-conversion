package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmCode {
    String code;
    String system;
    String display;
    String version;
    String _type;

   String  descriptor;
}
