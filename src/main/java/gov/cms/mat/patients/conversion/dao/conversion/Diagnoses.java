package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Diagnoses {
    String _id;
    String _type;
    QdmCodeSystem code;
    QdmCodeSystem presentOnAdmissionIndicator;
    Integer rank;
    String qdmVersion;
}
