package tedi.backend.repositories;

import tedi.backend.model.Booking;
import tedi.backend.model.Department;

import java.util.Date;

public interface BookingRepositoryCustom {

    Boolean BookingExists(Long depId , Date startDate , Date endDate);
    Boolean hasBooked(Long depId, Long userId);
    Boolean hasBookedAtLeastOne(Long tenantId, Long hostId);
}
