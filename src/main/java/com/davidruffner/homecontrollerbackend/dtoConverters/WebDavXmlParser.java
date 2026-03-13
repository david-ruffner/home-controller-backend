package com.davidruffner.homecontrollerbackend.dtoConverters;

import com.davidruffner.homecontrollerbackend.dtos.CalendarEventMultistatusResponse;
import com.davidruffner.homecontrollerbackend.dtos.CalendarGetCalendarsResponse;
import com.davidruffner.homecontrollerbackend.dtos.CalendarGetPrincipalServerResponse;
import com.davidruffner.homecontrollerbackend.dtos.CalendarGetUserInfoResponse;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import org.springframework.stereotype.Component;

@Component
public class WebDavXmlParser {

    private final XmlMapper xmlMapper;

    public WebDavXmlParser() {
        this.xmlMapper = XmlMapper.builder()
            .configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true)
            .build();
    }

    public CalendarGetUserInfoResponse.DavMultistatus parseMultistatus(String xml) throws ControllerException {
        try {
            return xmlMapper.readValue(xml, CalendarGetUserInfoResponse.DavMultistatus.class);
        } catch (Exception ex) {
            throw new ControllerException(ex.getMessage(), ResponseCode.SYSTEM_EXCEPTION,
                ShortCode.SYSTEM_EXCEPTION.toString());
        }
    }

    public CalendarGetPrincipalServerResponse.DavMultistatus parsePrincipalServerMultistatus(String xml)
        throws ControllerException {
        try {
            return xmlMapper.readValue(xml, CalendarGetPrincipalServerResponse.DavMultistatus.class);
        } catch (Exception ex) {
            throw new ControllerException(ex.getMessage(), ResponseCode.SYSTEM_EXCEPTION,
                ShortCode.SYSTEM_EXCEPTION.toString());
        }
    }

    public CalendarGetCalendarsResponse.DavMultistatus parseCalendarsMultistatus(String xml)
    throws ControllerException {
        try {
            return xmlMapper.readValue(xml, CalendarGetCalendarsResponse.DavMultistatus.class);
        } catch (Exception ex) {
            throw new ControllerException(ex.getMessage(), ResponseCode.SYSTEM_EXCEPTION,
                ShortCode.SYSTEM_EXCEPTION.toString());
        }
    }

    public CalendarEventMultistatusResponse.DavMultistatus parseEventsMultistatus(String xml)
        throws ControllerException {
        try {
            return xmlMapper.readValue(xml, CalendarEventMultistatusResponse.DavMultistatus.class);
        } catch (Exception ex) {
            throw new ControllerException(ex.getMessage(), ResponseCode.SYSTEM_EXCEPTION,
                ShortCode.SYSTEM_EXCEPTION.toString());
        }
    }
}
