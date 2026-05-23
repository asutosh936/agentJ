package com.agent.agentj.tool.impl;

import com.agent.agentj.tool.AgentTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool that returns the current date/time, optionally in a requested timezone.
 *
 * If Claude doesn't provide a timezone, it defaults to the system's local zone.
 * Supports any IANA timezone string like "America/New_York" or "Asia/Kolkata".
 */
@Slf4j
@Component
public class ClockTool implements AgentTool {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' HH:mm:ss z");

    @Override
    public String getName() {
        return "clock";
    }

    @Override
    public String getDescription() {
        return "Returns the current date and time. Optionally accepts a timezone " +
               "(IANA format, e.g. 'America/New_York', 'Asia/Kolkata'). " +
               "Defaults to the server's local timezone if none is specified.";
    }

    @Override
    public Map<String, Object> getInputSchema() {
        Map<String, Object> timezone = new LinkedHashMap<>();
        timezone.put("type", "string");
        timezone.put("description", "IANA timezone identifier, e.g. 'America/New_York'. Optional.");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("timezone", timezone);

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of()); // no required fields — timezone is optional
        return schema;
    }

    @Override
    public String execute(Map<String, Object> input) {
        String tz = (String) input.getOrDefault("timezone", null);
        ZoneId zoneId;
        if (tz != null && !tz.isBlank()) {
            try {
                zoneId = ZoneId.of(tz);
            } catch (Exception e) {
                return "Unknown timezone: '" + tz + "'. Use IANA format like 'America/New_York'.";
            }
        } else {
            zoneId = ZoneId.systemDefault();
        }
        String result = ZonedDateTime.now(zoneId).format(FORMATTER);
        log.info("[clock] timezone={} → {}", zoneId, result);
        return result;
    }
}
