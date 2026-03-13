package com.davidruffner.homecontrollerbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

public class CalendarGetCalendarsResponse {

    @JacksonXmlRootElement(localName = "multistatus")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavMultistatus(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "response")
        List<DavResponse> response
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavResponse(
        @JacksonXmlProperty(localName = "href")
        String href,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "propstat")
        List<DavPropstat> propstat
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavPropstat(
        @JacksonXmlProperty(localName = "prop")
        DavProp prop,

        @JacksonXmlProperty(localName = "status")
        String status
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavProp(
        @JacksonXmlProperty(localName = "displayname")
        String displayname,

        // CalDAV namespace in the XML, but Jackson will match by local name.
        @JacksonXmlProperty(localName = "calendar-description")
        String calendarDescription
    ) {}
}