package gov.cms.mat.patients.conversion.dao.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QdmComponent {
    String qdmVersion;
    String _type;
    String _id;


    //  QdmComponentResult result; //  Patient "_id": "5b61b638b84846662484a6c2" has a timestamp

    JsonNode result;

    QdmCode code;

    QdmPeriod referenceRange;
}
