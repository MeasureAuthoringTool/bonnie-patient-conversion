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
      <td><strong>Intervention, Performed & Procedure, Performed</strong></td>
      <td>Procedure</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Procedure.category</td>
      <td>Helps differentiate “intervention” from “procedure”</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>status</td>
      <td>Procedure.status</td>
      <td>constrain to “completed”</td>
      <td>Procedure.ProcedureStatus.COMPLETED</td>
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
      <td>&nbsp;</td>
      <td>Procedure.basedOn</td>
      <td>A reference to a resource that contains details of the request for this procedure.</td>
    </tr>
    <tr>
      <td>Method</td>
      <td>N/A</td>
      <td>Procedure.method does not exist in FHIR. Rather than create an extension, QI-Core’s approach is to assume the Procedure.code includes reference to the method.</td>
    </tr>
    <tr>
      <td>Rank</td>
      <td>Encounter.extension.extension:rank.value[x]:valuePositiveInt</td>
      <td>Referenced as attributes of Encounter (Encounter.extension.extension:rank.value[x]:valuePositiveInt).</td>
    </tr>
    <tr>
      <td>Priority</td>
      <td>qicore-encounter-procedure</td>
      <td>This QDM attribute is intended to reference elective from non-elective procedures.<br><br>QI-Core references procedure.priority based on the relationship of the procedure to the Encounter; hence, Encounter.procedure (which is an extension). <br><br>The elective nature of a procedure can also be referenced based on the elective nature of an Encounter (Encounter.priority) for which the respective procedure is a principal procedure.<br><br>The concept may also be addressed as an Encounter, Order or Procedure, Order (both using ServiceRequest) and ServiceRequest.priority.</td>
    </tr>
    <tr>
      <td>Anatomical Location Site</td>
      <td>Procedure.bodySite</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Reason</td>
      <td>Procedure.reasonCode</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getReason()</td>
    </tr>
    <tr>
      <td>Result</td>
      <td>Observation that includes the element Observation.partOf to reference the procedure to which it applies.</td>
      <td>Procedure.report references DiagnosticReport-note, DocumentReference, Composition (histology result, pathology report, surgical report, etc.); the latter two are not QI-Core resources. However, based on feedback regarding the use of the Observation resource, a procedure result might be better referenced as an Observation that includes the element Observation.partOf to reference the procedure to which it applies.</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.partOf</td>
      <td>Reference to a resource that contains details of the request for this procedure.</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td>See Below</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant dateTime</td>
      <td>Procedure.performed[x] dateTime</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Procedure.performed[x] Period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod()</td>
    </tr>
    <tr>
      <td>Incision dateTime</td>
      <td>Procedure.extension:incisionDateTime</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>N/A</td>
      <td>The concept “author” requires a reference to a report about the procedure or about an indication the procedure was not performed. Therefore, the procedure resource does not have a reference to author dateTime. Author dateTime can reference a report about the procedure or an observation describing that result (e.g., Observation with metadata Observation.partOf procedure). However, Procedure.statusReason needs to address a dateTime that it is recorded.</td>
    </tr>
    <tr>
      <td>Components</td>
      <td>N/A</td>
      <td>Procedure does not include components and the concept of components references a observation that is a result of the procedure (Observation.partOf) for which that observation has components consistent with the Observation component modeling recommendation in FHIR.</td>
    </tr>
    <tr>
      <td>Component code</td>
      <td>N/A</td>
      <td>N/A</td>
    </tr>
    <tr>
      <td>Component result</td>
      <td>N/A</td>
      <td>N/A</td>
    </tr>
    <tr>
      <td>Performer</td>
      <td>Procedure.performer.actor</td>
      <td>&nbsp;</td>
      <td>No data available for Performer</td>
    </tr>
  </tbody>
</table>