package pl.marcelgiercarz.elevatorsystemapi.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.CallStatus;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.ElevatorStatus;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorCallRepository;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElevatorServiceTest {
    @InjectMocks
    private ElevatorService elevatorService;
    @Mock
    private ElevatorRepository elevatorRepository;
    @Mock
    private ElevatorCallRepository elevatorCallRepository;
    @Mock
    private ElevatorDispatcher elevatorDispatcher;

    // floorRequest Tests
    @Test
    @DisplayName("It should throw EntityNotFoundException when elevator does not exist")
    void shouldThrowExceptionWhenElevatorNotFound(){
        when(elevatorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> elevatorService.floorRequest(1L, 5)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If elevator has no PENDING call, it should throw EntityNotFoundException")
    void shouldThrowExceptionWhenElevatorCallNotFound(){
        Elevator elevator = new Elevator();
        elevator.setId(1L);
        elevator.setCurrentFloor(5);

        when(elevatorRepository.findById(1L)).thenReturn(Optional.of(elevator));
        when(elevatorCallRepository.findElevatorCallByAssignedElevator_IdAndFloorAndStatus(1L, 5, CallStatus.PENDING)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> elevatorService.floorRequest(1L, 10)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("It should add target floor to queue and set call status to IN_PROGRESS")
    void shouldAddTargetFloorToQueueAndSetCallInProgress(){
        Elevator elevator = new Elevator();
        elevator.setId(1L);
        elevator.setCurrentFloor(5);
        elevator.setStopsQueue(new ArrayList<>());
        elevator.setDirection(Direction.UP);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(5);
        elevatorCall.setDirection(Direction.UP);
        elevatorCall.setAssignedElevator(elevator);
        elevatorCall.setStatus(CallStatus.PENDING);

        when(elevatorRepository.findById(1L)).thenReturn(Optional.of(elevator));
        when(elevatorCallRepository.findElevatorCallByAssignedElevator_IdAndFloorAndStatus(1L, 5, CallStatus.PENDING)).thenReturn(Optional.of(elevatorCall));

        elevatorService.floorRequest(1L, 10);
        assertThat(elevator.getStopsQueue()).contains(10);
        assertThat(elevatorCall.getStatus()).isEqualTo(CallStatus.IN_PROGRESS);
        assertThat(elevatorCall.getTargetFloor()).isEqualTo(10);
    }

    // simulateStepForAllElevators Tests
    @Test
    @DisplayName("It should set call to COMPLETED and elevator to IDLE on arrival at target floor")
    void shouldCompleteCallOnArrivalAtTargetFloor(){
        Elevator elevator = new Elevator();
        elevator.setId(1L);
        elevator.setStopsQueue(new ArrayList<>(List.of(5)));
        elevator.setStatus(ElevatorStatus.MOVING);
        elevator.setDirection(Direction.UP);
        elevator.setCurrentFloor(4);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setTargetFloor(5);
        elevatorCall.setStatus(CallStatus.IN_PROGRESS);
        elevatorCall.setAssignedElevator(elevator);
        elevatorCall.setDirection(Direction.UP);

        when(elevatorRepository.findAll()).thenReturn(List.of(elevator));
        when(elevatorCallRepository.findByTargetFloorAndStatusAndAssignedElevator(5, CallStatus.IN_PROGRESS, elevator)).thenReturn(List.of(elevatorCall));

        elevatorService.simulateStepForAllElevators();
        assertThat(elevatorCall.getStatus()).isEqualTo(CallStatus.COMPLETED);
        assertThat(elevator.getStopsQueue()).isEmpty();
        assertThat(elevator.getStatus()).isEqualTo(ElevatorStatus.IDLE);
        assertThat(elevator.getDirection()).isEqualTo(Direction.NONE);
    }


    // callElevator tests
    @Test
    @DisplayName("It should sort steps queue by SCAN algorithm when elevator is moving UP")
    void shouldSortQueueByScanAlgorithmWhenDirectionUp(){
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(5);
        elevator.setStatus(ElevatorStatus.MOVING);
        elevator.setStopsQueue(new ArrayList<>(List.of(8,4,2,1)));
        elevator.setDirection(Direction.UP);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(6);
        elevatorCall.setDirection(Direction.UP);

        when(elevatorDispatcher.dispatch(any())).thenReturn(elevator);
        when(elevatorCallRepository.save(any())).thenReturn(elevatorCall);

        elevatorService.callElevator(6, Direction.UP);

        assertThat(elevator.getStopsQueue()).containsExactly(6,8,4,2,1);
    }

    @Test
    @DisplayName("It should sort steps queue by SCAN algorithm when elevator is moving DOWN")
    void shouldSortQueueByScanAlgorithmWhenDirectionDown(){
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(5);
        elevator.setStatus(ElevatorStatus.MOVING);
        elevator.setStopsQueue(new ArrayList<>(List.of(8,4,2,1)));
        elevator.setDirection(Direction.DOWN);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.DOWN);

        when(elevatorDispatcher.dispatch(any())).thenReturn(elevator);
        when(elevatorCallRepository.save(any())).thenReturn(elevatorCall);

        elevatorService.callElevator(3, Direction.DOWN);

        assertThat(elevator.getStopsQueue()).containsExactly(4,3,2,1,8);
    }

    @Test
    @DisplayName("If elevator is IDLE, it should sort stops queue by SCAN and set direction to UP")
    void shouldSortQueueByScanAlgorithmWhenDirectionNone(){
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(5);
        elevator.setStatus(ElevatorStatus.IDLE);
        elevator.setStopsQueue(new ArrayList<>(List.of(8,10,2)));
        elevator.setDirection(Direction.NONE);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.UP);

        when(elevatorDispatcher.dispatch(any())).thenReturn(elevator);
        when(elevatorCallRepository.save(any())).thenReturn(elevatorCall);

        elevatorService.callElevator(6, Direction.UP);

        assertThat(elevator.getStopsQueue()).containsExactly(6,8,10,2);
        assertThat(elevator.getDirection()).isEqualTo(Direction.UP);
    }



}
