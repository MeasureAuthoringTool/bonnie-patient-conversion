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
      <td>Allergy/Intolerance</td>
      <td>AllergyIntolerance</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.clinicalStatus</td>
      <td>active, inactive, resolved</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.type</td>
      <td>Defines difference between Allergy and Intolerance</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.verificationStatus</td>
      <td>unconfirmed, confirmed, refuted, entered-in-error</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.extension:reasonRefuted</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.category</td>
      <td>Food, medication, environment, biologic</td>
    </tr>
    <tr>
      <td><strong>QDM Attributes</strong></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Code</td>
      <td>AllergyIntolerance.code</td>
      <td>RxNorm</td>
      <td>qdmDataElement.getDataElementCodes()</td>
    </tr>
    <tr>
      <td>id</td>
      <td>AllergyIntolerance.id</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.get_id()</td>
    </tr>
    <tr>
      <td>Prevalence Period</td>
      <td>AllergyIntolerance.onset[x]</td>
      <td>Prevalence Period start time maps to AllergyIntolerance.onset[x]. Implementers may need to “map” existing allergy onset timings (e.g., day, age, year, etc.) to a corresponding dateTime to allow calculation of measure or CDS expressions.</td>
      <td>qdmDataElement.getPrevalencePeriod()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.lastOccurrence</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.extension:resolutionAge</td>
      <td>Prevalence Period end time maps to AllergyIntolerance.extension:resolutionAge. Implementers may need to “map” existing allergy resolution timings (e.g., day, age, year, etc.) to a corresponding dateTime to allow calculation of measure or CDS expressions.</td>
    </tr>
    <tr>
      <td>author dateTime</td>
      <td>AllergyIntolerance.recordedDate</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getAuthorDatetime()</td>
    </tr>
    <tr>
      <td>Type</td>
      <td>AllergyIntolerance.reaction</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.reaction.substance</td>
      <td>&nbsp;</td>
      <td>qdmDataElement.getType()</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.reaction.manifestation</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.reaction.onset</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>Severity</td>
      <td>AllergyIntolerance.reaction.severity</td>
      <td>mild, moderate, severe</td>
      <td>Based on what factors we map it to AllergyIntolerance.reaction.severity or AllergyIntolerance.criticality<br><br>
      qdmDataElement.getSeverity()  is a code how to we map to a enum
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.criticality</td>
      <td>low, high, unable-to-assess</td>
    </tr>
    <tr>
      <td>Recorder</td>
      <td>AllergyIntolerance.asserter</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>AllergyIntolerance.recorder</td>
      <td>&nbsp;</td>
      <td>There is no Recorder attribute in qdmDataelements</td>
    </tr>
  </tbody>
</table>