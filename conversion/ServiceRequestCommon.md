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
      <td><strong>Used By Many</strong></td>
      <td>ServiceRequest</td>
      <td>&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>ServiceRequest.status</td>
      <td></td>
       <td>Set to Unknown unless negation</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>ServiceRequest.intent</td>
      <td></td>
        <td>Passwd as a parameter depends on the implementation</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Code</td>
      <td>ServiceRequest.code</td>
      <td>&nbsp;</td>
       <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>ServiceRequest.id</td>
      <td>&nbsp;</td>
     <td>  qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>Reason</td>
      <td>ServiceRequest.reasonCode</td>
      <td>&nbsp;</td>
         <td>qdmDataElement.getReason() </td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>ServiceRequest.authoredOn</td>
      <td>When the request transitioned to being actionable.</td>
         <td>qdmDataElement.getAuthorDatetime()</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td>See Below</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Requester</td>
      <td>ServiceRequest.requester</td>
      <td>&nbsp;</td>
        <td>Not mapped</td>
    </tr>
  </tbody>
</table>