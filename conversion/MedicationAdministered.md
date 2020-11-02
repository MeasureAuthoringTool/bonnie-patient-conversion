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
      <td><strong>Medication, Administered</strong></td>
      <td>MedicationAdministration</td>
      <td>&nbsp;</td>
      <td>QDM::MedicationAdministered</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>MedicationAdministration.status</td>
      <td>Constrain status to “In-progress” or “completed”</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>MedicationAdministration.category</td>
      <td>Allows specification of Inpatient, Outpatient, Community</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>MedicationAdministration.medication[x]</td>
      <td>ֵ Example uses SNOMED substance codes</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>MedicationAdministration.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>dosage</td>
      <td>MedicationAdministration.dosage.dose</td>
      <td>Simple Quantity - Amount of medication for one administration</td>
      <td>qdmDataElement.getDosage()</td>
    </tr>
    <tr>
      <td>route</td>
      <td>MedicationAdministration.dosage.route</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRoute()</td>
    </tr>
    <tr>
      <td>frequency</td>
      <td>MedicationAdministration.request</td>
      <td>Reference to original MedicationRequest with content about prescription</td>
      <td>This object alwaays contains all null elements -> qdmDataElement.getFrequency() currently Not mapped - is this a docu error should this go to  `MedicationAdministration.dosage.rate`</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>MedicationAdministration.dosage.rate[x]</td>
      <td>Identifies the speed with which the medication was or will be introduced into the patient (e.g., infusion rate).</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>MedicationRequest.dosageInstruction.timing</td>
      <td>Timing schedule (e.g., every 8 hours). MedicationAdministration.request provides reference to the applicable MedicationRequest for this information.</td>
    </tr>
    <tr>
      <td>reason</td>
      <td>MedicationAdministration.reasonCode</td>
      <td>None, given as ordered, emergency</td>
      <td>qdmDataElement.getReason()</td>
    </tr>
    <tr>
      <td>relevant dateTime</td>
      <td>MedicationAdministration.effective[x] dateTime</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantDatetime()</td>
    </tr>
    <tr>
      <td>relevant Period</td>
      <td>MedicationAdministration.effective[x] Period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod()</td>
    </tr>
    <tr>
      <td>author dateTime</td>
      <td>N/A</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td>See Below</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Performer</td>
      <td>MedicationAdministration.performer.actor</td>
      <td>&nbsp;</td>
      <td>No Data found qdmDataElement.getPerformer() </td>
    </tr>
  </tbody>
</table>