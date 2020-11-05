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
      <td><strong>Communication, Performed</strong></td>
      <td>Communication</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Communication.status</td>
      <td>consider constraining to in-progress, completed, on-hold</td>
      <td>If negation rationale is present set to `Communication.CommunicationStatus.NOTDONE` otherwise set to 
     ` Communication.CommunicationStatus.UNKNOWN`</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Code</td>
      <td>Communication.reasonCode</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Communication.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>category</td>
      <td>Communication.category</td>
      <td>alert, notification, reminder, instruction</td>
      <td>qdmDataElement.getCategory()</td>
    </tr>
    <tr>
      <td>medium</td>
      <td>Communication.medium</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getMedium()</td>
    </tr>
    <tr>
      <td>sent dateTime</td>
      <td>Communication.sent</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getSentDatetime()</td>
    </tr>
    <tr>
      <td>received dateTime</td>
      <td>Communication.received</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getReceivedDatetime()</td>
    </tr>
    <tr>
      <td>author dateTime</td>
      <td>N/A</td>
      <td>No timing exists in FHIR to address timing of negation (i.e., Communication.status = not-done</td>
    </tr>
    <tr>
      <td>relatedTo</td>
      <td>Communication.basedOn</td>
      <td>An order, proposal or plan fulfilled in whole or in part by this Communication.</td>
      <td>E.G.. this is example data in  5d63e5c8b848463cab3c7624 which is a QDM::InterventionPerformed  How to map. </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Communication.inResponseTo</td>
      <td>Response to a communication</td>
    </tr>
    <tr>
      <td>sender</td>
      <td>Communication.sender</td>
      <td>&nbsp;</td>
         <td>qdmDataElement.getSender  returns a QDM::Practitioner, Fhir wants reference - how to map <br>
          The sender/practitioner  **fwefwe** has this id not a momngo id </td>
    </tr>
    <tr>
      <td>recipient</td>
      <td>Communication.recipient</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRecipient()  returns a QDM::Practitioner, Fhir wants reference - how to map </td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td><a href="http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#871-communication-performed"> Click Here</a></td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>