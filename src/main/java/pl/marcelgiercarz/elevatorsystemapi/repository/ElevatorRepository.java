package pl.marcelgiercarz.elevatorsystemapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;


public interface ElevatorRepository extends JpaRepository<Elevator, Long> {

}
