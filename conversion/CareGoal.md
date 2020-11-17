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
      <td><strong>Care Goal</strong></td>
      <td>Goal</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Goal.achievementStatus</td>
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
      <td>Code</td>
      <td>Goal.target.measure</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Goal.target.detail[x]</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Goal.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>statusDate</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>     
      <td>&nbsp;</td>     
    </tr>
    <tr>
      <td>Target outcome</td>
      <td>Goal.target.detail[x]</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getTargetOutcome()</td>
    </tr>
    <tr>
      <td>Relevant Period</td>
      <td>Goal.start[x]</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod().getLow()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>Goal.target.due[x]</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>statusDate</td>
      <td>Goal.statusDate</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelevantPeriod().getHigh()</td>
    </tr>
    <tr>
      <td>relatedTo</td>
      <td>Goal.addresses</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getRelatedTo()</td>
    </tr>
    <tr>
      <td>Performer</td>
      <td>Goal.expressedBy</td>
      <td>&nbsp;</td>
      <td>No data for Performer qdmDataElement.getPerformer() </td>
    </tr>
  </tbody>
</table>

-----------
<pre>
goal.setSubject(createReference(fhirPatient))
</pre>