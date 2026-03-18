package pl.marcelgiercarz.elevatorsystemapi.service;

import org.springframework.stereotype.Service;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.ElevatorStatus;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Service
public class ElevatorDispatcher {
    private final ElevatorRepository elevatorRepository;

    public ElevatorDispatcher(ElevatorRepository elevatorRepository){
        this.elevatorRepository = elevatorRepository;
    }

    public Elevator dispatch(ElevatorCall call){
        List<Elevator> elevators = elevatorRepository.findAll();

        Optional<Elevator> onRouteElevator = elevators.stream()
                .filter(elevator -> isOnSamePath(elevator, call))
                .findFirst();
        if (onRouteElevator.isPresent()){
            return onRouteElevator.get();
        }

        Optional<Elevator> closestIdleElevator = elevators.stream()
                .filter(elevator -> elevator.getStatus() == ElevatorStatus.IDLE)
                .min(Comparator.comparingInt(elevator -> abs(elevator.getCurrentFloor() - call.getFloor())));
        if (closestIdleElevator.isPresent()){
            return closestIdleElevator.get();
        }

        Optional<Elevator> leastBusyElevator = elevators.stream()
                .min(Comparator.comparingInt(elevator -> elevator.getStopsQueue().size()));
        return leastBusyElevator.orElseThrow(() -> new IllegalStateException("Brak dostępnych wind"));
    }

    private boolean isOnSamePath(Elevator elevator, ElevatorCall call){
        return elevator.getStatus() == ElevatorStatus.MOVING
                && (elevator.getDirection() == Direction.DOWN && call.getFloor() < elevator.getCurrentFloor()
                || elevator.getDirection() == Direction.UP && call.getFloor() > elevator.getCurrentFloor());
    }

}
