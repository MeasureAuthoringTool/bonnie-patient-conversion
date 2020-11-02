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
      <td><strong>Family History</strong></td>
      <td>FamilyMemberHistory</td>
      <td>&nbsp;</td>
       <td>QDM::FamilyHistory</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>FamilyMemberHistory.status</td>
      <td>Constrain to partial, completed</td>
      <td>Set to FamilyMemberHistory.FamilyHistoryStatus.NULL - no unknown</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>FamilyMemberHistory.condition.code</td>
      <td>&nbsp;</td>
       <td>qdmDataElement.getDataElementCodes()) </td>
    </tr>
    <tr>
      <td>id</td>
      <td>FamilyMemberHistory.id</td>
      <td>&nbsp;</td>
       <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>FamilyMemberHistory.date</td>
      <td>&nbsp;</td>
       <td>qdmDataElement.getAuthorDatetime()</td>
    </tr>
    <tr>
      <td>relationship</td>
      <td>FamilyMemberHistory.relationship</td>
      <td>&nbsp;</td>
       <td>qdmDataElement.getRelationship() </td>
    </tr>
    <tr>
      <td>recorder</td>
      <td>N/A</td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>