<table class="grid">
  <thead>
    <tr>
      <th><strong>QDM Context</strong></th>
      <th><strong>QI-Core R4</strong></th>
      <th><strong>Comments</strong></th>
      <th>Conversion</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Encounter, Performed</strong></td>
      <td>Encounter</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.status</td>
      <td>consider constraint to - arrived, triaged, in-progress, on-leave, finished</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.type</td>
      <td>type of service by CPT</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><strong>QDM Attribute</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Code</td>
      <td>Encounter.class</td>
      <td>ambulatory, ED, inpatient, etc.</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Encounter.idt<td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Encounter.period</td>
      <td>start and end time of encounter</td>
      <td>&nbsp;qdmDataElement.getRelevantPeriod() </td>
    </tr>
    <tr>
      <td>Diagnoses</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Diagnosis (code)</td>
      <td>Encounter.diagnosis.condition</td>
      <td>can be used for coded diagnoses</td>
      <td>qdmDataElement.getDiagnoses()</td>
    </tr>
    <tr>
      <td>PresentOnAdmissionIndicator (code)</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Rank (Integer)</td>
      <td>Encounter.diagnosis.rank</td>
      <td>for each diagnosis role</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Procedures</td>
      <td>qicore-encounter-procedure</td>
      <td>QIcore-encounter-procedure</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.extension.procedure.value[x]</td>
      <td>References the procedure code</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.extension:rank.value[x]:valuePositiveInt</td>
      <td>References the rank; for principal procedure, the rank =1</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.procedure.procedure/td>
      <td>A reference to the procedure that was performed</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Length of Stay</td>
      <td>Encounter.length</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getLengthOfStay();</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td>Not Addressed</td>
      <td>There is no current use case for an eCQM to request a reason for failure to perform an encounter.</td>
      <td>&nbsp;Mamy QDM objects have this for QDM::EncounterPerformed create  message to user saying cannot convert </td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>Not Addressed</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Admission Source</td>
      <td>Encounter.hospitalization.admitSource</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Discharge Disposition</td>
      <td>Encounter.hospitalization.dischargeDisposition<</td>
      <td>E.g., home, hospice, long-term care, etc.</td>
      <td>qdmDataElement.getDischargeDisposition()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.hospitalizaton.destination</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Facility Locations</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>Encounter.location.location</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>location period</td>
      <td>Encounter.location.period</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Participant</td>
      <td>Encounter.participant.individual</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Encounter.serviceProvider</td>
      <td>Encounter.serviceProvider identifies the organization that is primarily responsible for the Encounter’s services.</td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>