package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.config.RestClientConfig;
import com.davidruffner.homecontrollerbackend.dtoConverters.WebDavXmlParser;
import com.davidruffner.homecontrollerbackend.dtos.*;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

@Service
public class CalendarService {

    static {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    }

    private static final String CALENDAR_NAME = "Calendar";

    @Autowired
    @Qualifier("CalendarRestClient")
    RestClient restClient;

    @Autowired
    WebDavXmlParser webDavXmlParser;

    @Autowired
    RestClientConfig restClientConfig;

    @Autowired
    Builder restClientBuilder;

    private static final DateTimeFormatter CALDAV_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

    public static List<VEvent> parseEvents(String ics) throws Exception {
        var builder = new CalendarBuilder();
        Calendar cal = builder.build(new StringReader(ics));

        return cal.getComponents(Component.VEVENT).stream()
            .map(c -> (VEvent) c)
            .toList();
    }

    /**
     * @param startTime {@code String} - Should be UTC-T (20260304T000000Z).
     * @param endTime {@code String} - Should be UTC-T.
     */
    public List<ICalEvent> getCalendarEvents(String startTime, String endTime) {
        // Add a day to the end time to prevent cut off from time zones
        // Filtering for the results will happen below
        Instant endInstant = Instant.from(CALDAV_FORMAT.parse(endTime));
        ZonedDateTime endZDT = endInstant.atZone(ZoneOffset.UTC)
            .plusDays(1);
        String paddedEndTime = endZDT.format(CALDAV_FORMAT);
        Integer endDayOfMonth = endZDT.getDayOfMonth() - 1; // Used below for filtering results

        Instant startInstant = Instant.from(CALDAV_FORMAT.parse(startTime));
        ZonedDateTime startZDT = startInstant.atZone(ZoneOffset.UTC);
        Integer startDayOfMonth = startZDT.getDayOfMonth(); // Used below for filtering results

        // Get User Information
        String userInfoRespBody = restClient
            .method(HttpMethod.valueOf("PROPFIND"))
            .uri("/")
            .headers(headers -> headers.setBasicAuth(
                "davidruffner@icloud.com",
                "ynts-mnbw-wnfd-dchh"
            ))
            .header("Depth", "0")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_XML)
            .body("""
                <d:propfind xmlns:d="DAV:">
                  <d:prop>
                    <d:current-user-principal/>
                  </d:prop>
                </d:propfind>
                """)
            .retrieve()
            .body(String.class);

        CalendarGetUserInfoResponse.DavMultistatus userInfoParsed =
            this.webDavXmlParser.parseMultistatus(userInfoRespBody);
        String userPrincipal = userInfoParsed.responses().get(0).propstats().get(0)
            .prop().currentUserPrincipal().href();

        // Get Principal Server
        String principalServerRespBody = restClient
            .method(HttpMethod.valueOf("PROPFIND"))
            .uri(userPrincipal)
            .headers(headers -> headers.setBasicAuth(
                "davidruffner@icloud.com",
                "ynts-mnbw-wnfd-dchh"
            ))
            .header("Depth", "0")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_XML)
            .body("""
                <d:propfind xmlns:d="DAV:" xmlns:cal="urn:ietf:params:xml:ns:caldav" 
                xmlns:cs="http://calendarserver.org/ns/">
                  <d:prop>
                    <cal:calendar-home-set/>
                  </d:prop>
                </d:propfind>
                """)
            .retrieve()
            .body(String.class);

        CalendarGetPrincipalServerResponse.DavMultistatus principalServerParsed =
            this.webDavXmlParser.parsePrincipalServerMultistatus(principalServerRespBody);
        String principalServer = principalServerParsed.responses().get(0).propstats().get(0)
            .prop().calendarHomeSet().href();
        String principalServerHost = "https://" + principalServer.split("https://")[1].split(":")[0].trim();
        RestClient customRestClient = this.restClientConfig.calendarRestClientCustom(
            restClientBuilder, principalServerHost);

        String calendarsListResp = customRestClient
            .method(HttpMethod.valueOf("PROPFIND"))
            .uri(principalServer)
            .headers(headers -> headers.setBasicAuth(
                "davidruffner@icloud.com",
                "ynts-mnbw-wnfd-dchh"
            ))
            .header("Depth", "1")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_XML)
            .body("""
                <propfind xmlns="DAV:" xmlns:cd="urn:ietf:params:xml:ns:caldav">
                  <prop>
                    <displayname/>
                    <cd:calendar-description/>
                  </prop>
                </propfind>
                """)
            .retrieve()
            .body(String.class);

        CalendarGetCalendarsResponse.DavMultistatus calendarsParsed =
            this.webDavXmlParser.parseCalendarsMultistatus(calendarsListResp);
        String calendarHref = calendarsParsed.response().stream()
            .filter(r -> r.propstat() != null)
            .filter(r -> r.propstat().stream()
                .map(CalendarGetCalendarsResponse.DavPropstat::prop)
                .filter(p -> p != null && p.displayname() != null)
                .anyMatch(p -> p.displayname().equals("Calendar")))
            .map(CalendarGetCalendarsResponse.DavResponse::href)
            .findFirst()
            .orElse(null);

        String calendarEventsResp = customRestClient
            .method(HttpMethod.valueOf("REPORT"))
            .uri(calendarHref)
            .headers(headers -> headers.setBasicAuth(
                "davidruffner@icloud.com",
                "ynts-mnbw-wnfd-dchh"
            ))
            .header("Depth", "1")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_XML)
            .body(String.format("""
                <c:calendar-query xmlns:d="DAV:"
                                  xmlns:c="urn:ietf:params:xml:ns:caldav">
                  <d:prop>
                    <d:getetag/>
                    <c:calendar-data>
                        <c:expand start="%s"
                                      end="%s"/>
                    </c:calendar-data>
                  </d:prop>
                
                  <c:filter>
                    <c:comp-filter name="VCALENDAR">
                      <c:comp-filter name="VEVENT">
                        <c:time-range start="%s"
                                      end="%s"/>
                      </c:comp-filter>
                    </c:comp-filter>
                  </c:filter>
                
                </c:calendar-query>
                """, startTime, paddedEndTime, startTime, paddedEndTime))
            .retrieve()
            .body(String.class);

        CalendarEventMultistatusResponse.DavMultistatus eventsParsed =
            this.webDavXmlParser.parseEventsMultistatus(calendarEventsResp);

        List<ICalEvent> eventsList = new ArrayList<>();
        eventsParsed.responses().forEach(resp ->
            resp.propstats().forEach(ps -> {
                try {
                    parseEvents(ps.prop().calendarData()).forEach(event -> {
                        int currentEndDayOfMonth = event.getEndDate().get().getDate().get(ChronoField.DAY_OF_MONTH);
                        int currentStartDayOfMonth = event.getStartDate().get().getDate().get(ChronoField.DAY_OF_MONTH);

                        if (currentEndDayOfMonth < endDayOfMonth && currentStartDayOfMonth >= startDayOfMonth) {
                            eventsList.add(new ICalEvent(event));
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
        );

        return eventsList;
    }
}
