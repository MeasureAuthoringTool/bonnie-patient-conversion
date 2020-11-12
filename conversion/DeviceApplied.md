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
      <td><strong>Device, Applied</strong></td>
      <td>Procedure</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Procedure.status</td>
      <td>Constrain to “active”</td>
      <td>Status is set to UNKNOWN</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Procedure.usedCode</td>
      <td>Specify the device (direct reference code or value set) used as part of the procedure</td>
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
      <td>Procedure.code</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Procedure.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>Anatomical Location Site</td>
      <td>Procedure.bodySite</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Reason</td>
      <td>Procedure.reasonCode</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getReason()</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td><a href="http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8911-negation-rationale-for-device-applied">Click Here</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Procedure.performed[x] Period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod()</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>N/A</td>
      <td>Only used when negated</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Performer</td>
      <td>Procedure.performer.actor</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>