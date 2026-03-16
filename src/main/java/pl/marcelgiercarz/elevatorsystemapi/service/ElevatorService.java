package pl.marcelgiercarz.elevatorsystemapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.CallStatus;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.ElevatorStatus;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorCallRepository;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
//@RequiredArgsConstructor
public class ElevatorService {

    private final ElevatorRepository elevatorRepository;
    private final ElevatorCallRepository elevatorCallRepository;
    private final ElevatorDispatcher elevatorDispatcher;

    public ElevatorService(ElevatorRepository elevatorRepository,
                           ElevatorCallRepository elevatorCallRepository,
                           ElevatorDispatcher elevatorDispatcher) {

        this.elevatorRepository = elevatorRepository;
        this.elevatorCallRepository = elevatorCallRepository;
        this.elevatorDispatcher = elevatorDispatcher;
    }

    public List<Elevator> getAllElevators(){
        return elevatorRepository.findAll();
    }

    public Optional<Elevator> getElevatorById(Long id){
        return elevatorRepository.findById(id);
    }

    public Elevator addElevator(){
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(0);
        elevator.setDirection(Direction.NONE);
        elevator.setStatus(ElevatorStatus.IDLE);
        return elevatorRepository.save(elevator);
    }

    public ElevatorCall callElevator(int floor, Direction direction){
        ElevatorCall elevatorCall = new ElevatorCall();

        elevatorCall.setDirection(direction);
        elevatorCall.setFloor(floor);
        elevatorCall.setStatus(CallStatus.PENDING);
        elevatorCallRepository.save(elevatorCall);

        Elevator assignedElevator = elevatorDispatcher.dispatch(elevatorCall);
        sortFloorsQueue(assignedElevator);
        elevatorRepository.save(assignedElevator);

        return elevatorCallRepository.save(elevatorCall);
    }

    public Elevator floorRequest(Long elevatorId, int targetFloor){
        Elevator elevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new EntityNotFoundException("Elevator not found: " + elevatorId));
        int currentFloor = elevator.getCurrentFloor();

        ElevatorCall elevatorCall = elevatorCallRepository
                .findElevatorCallByAssignedElevator_IdAndFloorAndStatus(elevatorId, currentFloor, CallStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Elevator call not found, Elevator ID: %d, Current floor: %d, Call status: %s", elevatorId, currentFloor, CallStatus.PENDING)));

        elevator.getStopsQueue().add(targetFloor);
        elevatorCall.setStatus(CallStatus.IN_PROGRESS);
        elevatorCall.setTargetFloor(targetFloor);

        elevatorRepository.save(elevator);
        elevatorCallRepository.save(elevatorCall);
        return elevator;
    }

    private void simulateStep(Elevator elevator){
        if (elevator.getStopsQueue().isEmpty()){
            return;
        }

        int targetFloor = elevator.getStopsQueue().getFirst();
        int currentFloor = elevator.getCurrentFloor();

        if (targetFloor > currentFloor){
            elevator.setDirection(Direction.UP);
            elevator.setStatus(ElevatorStatus.MOVING);
            elevator.setCurrentFloor(++currentFloor);
        } else if (targetFloor < currentFloor) {
            elevator.setDirection(Direction.DOWN);
            elevator.setStatus(ElevatorStatus.MOVING);
            elevator.setCurrentFloor(--currentFloor);
        }
        handleArrival(elevator, targetFloor);
        elevatorRepository.save(elevator);

    }

    private void handleArrival(Elevator elevator, int targetFloor){
        if (elevator.getCurrentFloor() == targetFloor){
            elevatorCallRepository.findByTargetFloorAndStatus(targetFloor, CallStatus.IN_PROGRESS)
                    .forEach(call -> {
                        call.setStatus(CallStatus.COMPLETED);
                        elevatorCallRepository.save(call);
                    });
            elevator.getStopsQueue().removeFirst();
            if (elevator.getStopsQueue().isEmpty()){
                elevator.setStatus(ElevatorStatus.IDLE);
                elevator.setDirection(Direction.NONE);
            }
        }
    }



    public List<Elevator> simulateStepForAllElevators(){
        List<Elevator> elevatorsList = getAllElevators();
        elevatorsList.forEach(this::simulateStep);
        return elevatorsList;
    }

    private void sortFloorsQueue(Elevator elevator){
        int currentFloor = elevator.getCurrentFloor();

        if (elevator.getDirection() == Direction.NONE) {
            if (elevator.getStopsQueue().stream()
                .anyMatch(floor -> floor>currentFloor)){
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.DOWN);
            }
        }

        List<Integer> floorsAboveCurrentFloor = elevator.getStopsQueue().stream()
                .filter(floor -> floor > currentFloor)
                .sorted()
                .toList();
        List<Integer> floorsBelowCurrentFloor = elevator.getStopsQueue().stream()
                .filter(floor -> floor < currentFloor)
                .sorted(Comparator.reverseOrder())
                .toList();
        List<Integer> sortedFloors = elevator.getDirection() == Direction.UP
                ? Stream.concat(floorsAboveCurrentFloor.stream(), floorsBelowCurrentFloor.stream()).toList()
                : Stream.concat(floorsBelowCurrentFloor.stream(), floorsAboveCurrentFloor.stream()).toList();
        elevator.setStopsQueue(new ArrayList<>(sortedFloors));
    }


    public List<ElevatorCall> getAllElevatorCalls(){
        return elevatorCallRepository.findAll();
    }



}
