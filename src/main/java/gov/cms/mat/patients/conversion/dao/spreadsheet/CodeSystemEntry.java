package gov.cms.mat.patients.conversion.dao.spreadsheet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CodeSystemEntry implements Comparable<CodeSystemEntry> {
    private String oid;
    private String url;
    private String name;

    @Override
    public int compareTo(CodeSystemEntry rhs) {
        return this.name.compareToIgnoreCase(rhs.name);
    }
}
