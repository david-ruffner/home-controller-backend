package com.davidruffner.homecontrollerbackend.dtos;

import net.fortuna.ical4j.model.component.VEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.davidruffner.homecontrollerbackend.utils.Utils.parseIsoDateToDate;

public class ICalEvent {
    public static class Location {
        private final String name;
        private final String address;
        private final String city;
        private final String state;
        private final String zipCode;
        private final String country;

        public Location(String locationStr) {
            this.name = locationStr.split("\n")[0].trim();
            String addressStr = locationStr.split("\n")[1].trim();
            String[] addressStrSplit = addressStr.split(",");

            this.address = addressStrSplit[0].trim();
            this.city = addressStrSplit[1].trim();
            this.state = addressStrSplit[2].trim().split(" ")[0].trim();
            this.zipCode = addressStrSplit[2].trim().split(" ")[1].trim();
            this.country = addressStrSplit[3].trim();
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCountry() {
            return country;
        }
    }

    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final Date startDate;
    private final Date endDate;
    private final String timeZone;
    private final Location location;
    private final String summary;
    private final Boolean isAllDay;

    public ICalEvent(VEvent vEvent) {
        if (vEvent.getDateTimeStart().get().getParameter("TZID").isPresent()) {
            this.timeZone = vEvent.getDateTimeStart().get().getParameter("TZID").get().getValue();

            // Convert start date/time
            ZoneId startZone = ZoneId.of(this.timeZone);
            Instant startInstant = Instant.from(vEvent.getDateTimeStart().get().getDate());
            this.startDateTime = startInstant.atZone(startZone);

            // Convert end date/time
            ZoneId endZone = ZoneId.of(this.timeZone);
            Instant endInstant = Instant.from(vEvent.getDateTimeEnd().get().getDate());
            this.endDateTime = endInstant.atZone(endZone);

            this.startDate = null;
            this.endDate = null;
            this.isAllDay = false;
        } else {
            this.startDate = parseIsoDateToDate(vEvent.getDateTimeStart().get().getDate().toString(),
                "America/Detroit");
            this.endDate = parseIsoDateToDate(vEvent.getDateTimeEnd().get().getDate().toString(),
                "America/Detroit");

            this.startDateTime = null;
            this.endDateTime = null;
            this.timeZone = null;
            this.isAllDay = true;
        }

        // Convert location
        this.location = vEvent.getLocation().isPresent() ?
            new Location(vEvent.getLocation().get().getValue()) : null;

        // Grab summary
        this.summary = vEvent.getSummary().isPresent() ?
            vEvent.getSummary().get().getValue() : null;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public Location getLocation() {
        return location;
    }

    public String getSummary() {
        return summary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }
}
