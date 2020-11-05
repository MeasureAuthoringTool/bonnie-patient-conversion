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
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Procedure.category</td>
      <td>Helps differentiate “intervention” from “procedure”</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
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
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Method</td>
      <td>N/A</td>
      <td>Procedure.method does not exist in FHIR. Rather than create an extension, QI-Core’s approach is to assume the Procedure.code includes reference to the method.</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Rank</td>
      <td>Encounter.extension.extension:rank.value[x]:valuePositiveInt</td>
      <td>Referenced as attributes of Encounter (Encounter.extension.extension:rank.value[x]:valuePositiveInt).</td>
      <td>Need stan ? How to add rank in a Encounter Extension? See comments below from Stan</td>
    </tr>
    <tr>
      <td>Priority</td>
      <td>qicore-encounter-procedure</td>
      <td>This QDM attribute is intended to reference elective from non-elective procedures.<br><br>QI-Core references procedure.priority based on the relationship of the procedure to the Encounter; hence, Encounter.procedure (which is an extension). <br><br>The elective nature of a procedure can also be referenced based on the elective nature of an Encounter (Encounter.priority) for which the respective procedure is a principal procedure.<br><br>The concept may also be addressed as an Encounter, Order or Procedure, Order (both using ServiceRequest) and ServiceRequest.priority.</td>
      <td>Need stan ? Comments make little sense to non expert? See comments below from Stan </td>
    </tr>
    <tr>
      <td>Anatomical Location Site</td>
      <td>Procedure.bodySite</td>
      <td>&nbsp;</td>
      <td>Not mapped</td>
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
       <td>Need stan ? We have results not sure how to map comments confusing? See comments below from Stan</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.partOf</td>
      <td>Reference to a resource that contains details of the request for this procedure.</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td><a href="http://hl7.org/fhir/us/qicore/qdm-to-qicore.html#8204-procedure-performed"> Click Here </a> </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant dateTime</td>
      <td>Procedure.performed[x] dateTime</td>
      <td>&nbsp;</td>
     <td>   qdmDataElement.getRelevantDatetime() </td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Procedure.performed[x] Period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod() -- ****Relevant dateTime< and Relevant Period map to the same Type object in java hapi fhir, If we have both we take period since it has more data****  </td>
    </tr>
    <tr>
      <td>Incision dateTime</td>
      <td>Procedure.extension:incisionDateTime</td>
      <td>&nbsp;</td>
      <td>Map to extension</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>N/A</td>
      <td>The concept “author” requires a reference to a report about the procedure or about an indication the procedure was not performed. Therefore, the procedure resource does not have a reference to author dateTime. Author dateTime can reference a report about the procedure or an observation describing that result (e.g., Observation with metadata Observation.partOf procedure). However, Procedure.statusReason needs to address a dateTime that it is recorded.</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Components</td>
      <td>N/A</td>
      <td>Procedure does not include components and the concept of components references a observation that is a result of the procedure (Observation.partOf) for which that observation has components consistent with the Observation component modeling recommendation in FHIR.</td>
      <td>Need stan ? We have components not sure how to map comments confusing? See comments below from Stan</td>
    </tr>
    <tr>
      <td>Component code</td>
      <td>N/A</td>
      <td>N/A</td>
      <td>N/A</td>
    </tr>
    <tr>
      <td>Component result</td>
      <td>N/A</td>
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

<head>
    <strong>Comments from Stan Ratkins</strong>``
</head>
<br>
<br>
<pre>
    QDM Attribute Rank - So this would require support of bi-directional references in Bonnie - procedure that references an encounter that 
    references the containing  procedure. That's because principal procedure (rank = 1) does not have any context outside of an Encounter. 
    Although the GUI wouldn't need to show this complexity, the backend would need to track it. According to our meeting yesterday, 
    we said that we were not going to support bi-directional references for Beta. Do we now want to support bi-directional references?
</pre>
<br>
<pre>
    QDM Attribute Priority - Same issue as 1, but the good thing is that user's of QDM are being told not to use priority
</pre>
<br>
<pre>
    QDM Attribute Result - My recommendation - skip it - don't have all the data. Rationale: Procedure Result is actually an Observation.value that is part of 
    (i.e., Observation.partOf) a referenced procedure. In other words, parent resource would be an Observation that references a procedure using partOf; 
    that Observation would require a code (not in the current data set) and it's associated value (which would be coming from the QDM result attribute).
</pre>
<br>
<pre>
    QDM Attribute Components - We don't have all the data needed in QDM to do this mapping, so I recommend skipping this too.
</pre>