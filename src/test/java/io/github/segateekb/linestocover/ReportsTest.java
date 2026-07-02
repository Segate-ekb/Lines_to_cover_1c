package io.github.segateekb.linestocover;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportsTest {

    @Test
    void jsonСодержитПутьИСтроки() {
        Map<String, int[]> data = new LinkedHashMap<>();
        data.put("cf/CommonModules/М/Module.bsl", new int[]{2, 3});
        String json = Reports.json(data);
        assertTrue(json.contains("\"cf/CommonModules/М/Module.bsl\": [2, 3]"), json);
    }

    @Test
    void genericCoverageПомечаетСтрокиНепокрытыми() {
        Map<String, int[]> data = new LinkedHashMap<>();
        data.put("cf/CommonModules/М/Module.bsl", new int[]{2});
        String xml = Reports.genericCoverage(data);
        assertTrue(xml.contains("<coverage version=\"1\">"), xml);
        assertTrue(xml.contains("<file path=\"cf/CommonModules/М/Module.bsl\">"), xml);
        assertTrue(xml.contains("lineNumber=\"2\" covered=\"false\""), xml);
    }
}
