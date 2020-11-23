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
      <td>Adverse Event</td>
      <td>AdverseEvent</td>
      <td>&nbsp;</td>
      <td>QDM::AdverseEvent</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AdverseEvent.actuality</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>AdverseEvent.event</td>
      <td>FHIR R4 replaces AdverseEvent.type with AdverseEvent.event</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>type</td>
      <td>AdverseEvent.category</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getType() - No data for Category</td>
    </tr>
    <tr>
      <td>severity</td>
      <td>AdverseEvent.severity</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getSeverity() - No data for severity</td>
    </tr>
    <tr>
      <td>relevant dateTime</td>
      <td>AdverseEvent.date</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantDatetime()</td>
    </tr>
    <tr>
      <td>FacilityLocations</td>
      <td>AdverseEvent.location</td>
      <td>&nbsp;</td>
      <td>No data for Locations and to map to fhir needs a reference</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>AdverseEvent.recordedDate</td>
      <td>&nbsp;</td>
      <td>getAuthorDatetime() -No data for Author dateTime</td>
    </tr>
    <tr>
      <td>id</td>
      <td>AdverseEVent.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>recorder</td>
      <td>AdverseEvent.recorder</td>
      <td>&nbsp;</td>
      <td>Not Mapped</td>
    </tr>
  </tbody>
</table>

<pre>
adverseEvent.setSubject(createPatientReference(fhirPatient));
</pre>