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
      <td><strong>Condition - Diagnosis</strong></td>
      <td>Condition</td>
      <td>&nbsp;</td>
      <td>QDM::Diagnosis</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Condition.clinicalStatus</td>
      <td>defines active/inactive</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Condition.verificationStatus</td>
      <td>confirmed, unconfirmed provisional, differential, refuted, entered-in-error,</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Condition.category</td>
      <td>problem-list-item, encounter-diagnosis, health-concern</td>
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
      <td>Condition.code</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Condition.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>Prevalence Period</td>
      <td>Condition.onset[x]</td>
      <td>May be dateTime, Age, Period Range, string</td>
      <td>qdmDataElement.getPrevalencePeriod()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Condition.abatement[x]</td>
      <td>May be dateTime, Age, Period Range, string</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>Condition.recordedDate</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getAuthorDatetime() -- usually comes in as null</td>
    </tr>
    <tr>
      <td>Severity</td>
      <td>Condition.severity</td>
      <td>severe, moderate, mild</td>
      <td>qdmDataElement.getSeverity()</td>
    </tr>
    <tr>
      <td>Anatomical Location Site</td>
      <td>Condition.bodySite</td>
      <td>&nbsp;</td>
      <td>&nbsp;Not mapped </td>
    </tr>
    <tr>
      <td>Recorder</td>
      <td>Condition.recorder</td>
      <td>Individual who recorded the record and takes responsibility for its content</td>
      <td>&nbsp;Not mapped </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Condition.asserter</td>
      <td>Individual who is making the condition statement</td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>

<pre>
condition.setSubject(createPatientReference(fhirPatient));
</pre>