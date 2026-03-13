package com.davidruffner.homecontrollerbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

public class CalendarGetPrincipalServerResponse {
    @JacksonXmlRootElement(localName = "multistatus") // namespace is handled by mapper features below
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
        @JacksonXmlProperty(localName = "displayname")
        String displayname,

        // Some DAV fields are nested (e.g. <current-user-principal><href>...</href></current-user-principal>)
        @JsonProperty("calendar-home-set")
        DavHrefContainer calendarHomeSet
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DavHrefContainer(
        @JacksonXmlProperty(localName = "href")
        String href
    ) {}
}
