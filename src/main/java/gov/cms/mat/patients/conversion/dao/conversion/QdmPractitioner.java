package gov.cms.mat.patients.conversion.dao.conversion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmPractitioner {
    String _id;
    String qdmVersion;
    String _type;

    String type;

    QdmCodeSystem role;
    QdmCodeSystem specialty;
    QdmCodeSystem qualification;

    QdmIdentifier identifier;
    String hqmfOid;
    String qrdaOid;
}
