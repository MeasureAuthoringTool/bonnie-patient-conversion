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
      <td><strong>Assessment, Performed: General Use Case</strong></td>
      <td>Observation</td>
      <td>&nbsp;</td>
      <td>QDM:AssessmentPerformed</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.category</td>
      <td>Since Assessment is a broad concept, the measure developer will need to select the appropriate category.</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.status</td>
      <td>Constrain status to -&nbsp; final, amended, corrected</td>
      <td>Observation.ObservationStatus.UNKNOWN. If Negation Rational is not null, then status is set to Observation.ObservationStatus.FINAL</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>code</td>
      <td>Observation.code</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Observation.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>method</td>
      <td>Observation.method</td>
      <td>&nbsp;</td>
      <td>No data for qdmDataElement.getMethod()</td>
    </tr>
    <tr>
      <td>relatedTo</td>
      <td>Observation.basedOn</td>
      <td>&nbsp;</td>
      <td>No data for qdmDataElement.getRelatedTo()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.partOf</td>
      <td>A larger event of which this particular Observation is a component or step. For example, an observation as part of a procedure.</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.derivedFrom</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Negation Rationale</td>
      <td>See Below</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>reason</td>
      <td>Observation.basedOn</td>
      <td>The observation fulfills a plan, proposal or order - trace for authorization. Not a perfect&nbsp; fit for the intent in QDM (e.g., observation “reason” = a diagnosis)&nbsp; Is an extension needed?</td>
      <td>No data for qdmDataElement.getReason()</td>
    </tr>
    <tr>
      <td>result</td>
      <td>Observation.value[x]</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getResult()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.interpretation</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Relevant dateTime</td>
      <td>Observation.effective[x] dateTime</td>
      <td>&nbsp;</td>
      <td>No data for qdmDataElement.getRelevantDateTime()</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Observation.effective[x] Period</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod()</td>
    </tr>
    <tr>
      <td>Author dateTime</td>
      <td>Observation.issued</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getAuthorDatetime() OR qdmDataElement.getResultDatetime()</td>
    </tr>
    <tr>
      <td>Component</td>
      <td>Observation.component</td>
      <td>&nbsp;</td>
      <td>List&lt;Observation.ObservationComponentComponent&gt;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.component.id</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Component code</td>
      <td>Observation.component.code</td>
      <td>&nbsp;</td>
      <td>A new codeSystem was created and converted into CodeableConcept</td>
    </tr>
    <tr>
      <td>Component result</td>
      <td>Observation.component.value[x]</td>
      <td>&nbsp;</td>
      <td>qdmComponent.getResult()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.component.interpretation</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Observation.component.dataAbsentReason</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Performer</td>
      <td>Observation.performer</td>
      <td>&nbsp;</td>
      <td>No data for qdmDataElement.getPerformer()</td>
    </tr>
  </tbody>
</table>

----
observation.setSubject(createReference(fhirPatient));