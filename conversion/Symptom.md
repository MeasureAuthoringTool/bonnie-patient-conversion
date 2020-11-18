<table class="grid">
  <thead>
    <tr>
      <th><strong>QDM Context</strong></th>
      <th><strong>QI-Core R4</strong></th>
      <th><strong>Comments</strong></th>
      <th><strong>Conversion</strong></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Symptom</strong></td>
      <td>Observation</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.status</td>
      <td>restrict to preliminary, final, amended, corrected</td>
      <td>Observation.ObservationStatus.UNKNOWN</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.category</td>
      <td>add symptom concept</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Code</td>
      <td>Observation.value[x]</td>
      <td>Use codable concept</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Observation.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>severity</td>
      <td>Observation.interpretation</td>
      <td>Definition suggests high, low, normal - perhaps consider severe, moderate, mild.</td>
      <td>No data for qdmDataElement.getSeverity()</td>
    </tr>
    <tr>
      <td>prevalence period</td>
      <td>Observation.effective[x]</td>
      <td>dateTime, period, timing, instant</td>
      <td>qdmDataElement.getPrevalencePeriod()</td>
    </tr>
    <tr>
      <td>recorder</td>
      <td>Observation.performer</td>
      <td>&nbsp;</td>
      <td>&nbsp;Not mapped </td>
    </tr>
  </tbody>
</table>