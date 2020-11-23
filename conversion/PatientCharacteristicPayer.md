<table class="grid">
  <thead>
    <tr>
      <th><strong>QDM Attribute</strong></th>
      <th><strong>US Core R4</strong></th>
      <th><strong>Comments</strong></th>
       <th><strong>Conversion</strong></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Payer</strong></td>
      <td>Coverage</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>Coverage.payor</td>
      <td>CQI-Core currently maps to policy holder which actually references the person who owns the policy, not the payor.</td>
      <td>qdmDataElement.getDataElementCodes() mapped to coverage.getType() <br>
      How to map code --> Coverage.payor ??</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Coverage.period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Coverage.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getId()</td>
    </tr>
  </tbody>
</table>