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
      <td>See Below</td>
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
<table class="grid">
    <H4><strong>Negation Rationale</strong></H4>
    <thead>
        <tr>
            <th><strong>QI-Core R4</strong></th>
            <th><strong>Comments</strong></th>
            <th><strong>Conversion</strong></th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Procedure.status</td>
            <td>Fixed as “not-done”</td>
            <td>Procedure.ProcedureStatus.NOTDONE</td>
        </tr>
        <tr>
            <td>Procedure.statusReason</td>
            <td>Use value set NegationReasonCodes</td>
            <td>&nbsp;</td>          
        </tr>
        <tr>
            <td>Procedure.extension:recorded</td>
            <td>When this was made available</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>Procedure.code</td>
            <td>Use Procedure.code.coding.extension:notDoneValueSet to indicate the specific Procedure that was not performed</td>
            <td>Created new Extension with QICORE_RECORDED url and 
            qdmDataElement.getAuthorDatetime()</td>
        </tr> 
    </tbody>
</table>  