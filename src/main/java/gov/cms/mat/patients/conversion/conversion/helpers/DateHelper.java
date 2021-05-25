package gov.cms.mat.patients.conversion.conversion.helpers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public interface DateHelper {

    default Date convertToDateFromObjectNode(ObjectNode objectNode) {

        if (objectNode.has("year") && objectNode.has("month") && objectNode.has("day")) {
            var calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            calendar.set(objectNode.get("year").asInt(),
                    objectNode.get("month").asInt(),
                    objectNode.get("day").asInt());


            return calendar.getTime();
        } else {
            return null;
        }
    }

    default Date convertToDateFromTextNode(TextNode textNode) {
        if (StringUtils.isEmpty(textNode.asText())) {
            return null;
        }

        try {
            //"2012-01-16",
            var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
          return   dateFormat.parse(textNode.asText());
        } catch (Exception e) {
            //ignored
            return null;
        }
    }
}

