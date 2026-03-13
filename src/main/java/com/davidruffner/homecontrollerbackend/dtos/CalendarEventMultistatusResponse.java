package com.davidruffner.homecontrollerbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

public class CalendarEventMultistatusResponse {

    @JacksonXmlRootElement(localName = "multistatus")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavMultistatus(

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "response")
        List<DavResponse> responses

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavResponse(

        @JacksonXmlProperty(localName = "href")
        String href,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "propstat")
        List<DavPropstat> propstats

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavPropstat(

        @JacksonXmlProperty(localName = "status")
        String status,

        @JacksonXmlProperty(localName = "prop")
        DavProp prop

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavProp(

        @JacksonXmlProperty(localName = "getetag")
        String getetag,

        @JacksonXmlProperty(localName = "calendar-data")
        String calendarData

    ) {}
}
