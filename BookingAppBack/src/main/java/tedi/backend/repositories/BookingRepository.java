package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom{
}
