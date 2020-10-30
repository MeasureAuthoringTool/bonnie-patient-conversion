package gov.cms.mat.patients.conversion.dao.spreadsheet;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CodeSystemEntry implements Comparable<CodeSystemEntry> {
    private String oid;
    private String url;
    private String name;
    private String defaultVsacVersion;

    @Override
    public int compareTo(CodeSystemEntry rhs) {
        return this.name.compareToIgnoreCase(rhs.name);
    }
}
