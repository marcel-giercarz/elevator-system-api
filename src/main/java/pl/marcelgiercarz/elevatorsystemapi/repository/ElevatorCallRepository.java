package pl.marcelgiercarz.elevatorsystemapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.CallStatus;

import java.util.List;
import java.util.Optional;

public interface ElevatorCallRepository extends JpaRepository<ElevatorCall, Long> {

    List<ElevatorCall> findByAssignedElevatorAndFloor(Elevator elevator, int floor);

    Optional<ElevatorCall> findElevatorCallByAssignedElevator_IdAndFloorAndStatus(Long elevatorId, int floor, CallStatus status);

    List<ElevatorCall> findByTargetFloorAndStatus(Integer targetFloor, CallStatus status);
}
