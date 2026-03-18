package pl.marcelgiercarz.elevatorsystemapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.ElevatorStatus;
import pl.marcelgiercarz.elevatorsystemapi.repository.ElevatorRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElevatorDispatcherTest {

    @InjectMocks
    private ElevatorDispatcher dispatcher;
    @Mock
    private ElevatorRepository elevatorRepository;

    @Test
    @DisplayName("It should assign elevator moving in the same direction")
    void shouldAssignMovingElevatorOnSamePath(){
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(5);
        elevator.setStatus(ElevatorStatus.MOVING);
        elevator.setStopsQueue(new ArrayList<>());
        elevator.setDirection(Direction.DOWN);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.DOWN);

        when(elevatorRepository.findAll()).thenReturn(List.of(elevator));

        Elevator dispatchedElevator = dispatcher.dispatch(elevatorCall);

        assertThat(dispatchedElevator).isEqualTo(elevator);
    }

    @Test
    @DisplayName("If there is no moving elevator in same direction, it should assign nearest IDLE elevator")
    void shouldAssignNearestIdleElevatorWhenNoElevatorOnSamePath(){
        Elevator elevator1 = new Elevator();
        elevator1.setCurrentFloor(8);
        elevator1.setStatus(ElevatorStatus.IDLE);
        elevator1.setStopsQueue(new ArrayList<>());
        elevator1.setDirection(Direction.NONE);

        Elevator elevator2 = new Elevator();
        elevator2.setCurrentFloor(5);
        elevator2.setStatus(ElevatorStatus.IDLE);
        elevator2.setStopsQueue(new ArrayList<>());
        elevator2.setDirection(Direction.NONE);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.DOWN);

        when(elevatorRepository.findAll()).thenReturn(List.of(elevator1, elevator2));

        Elevator dispatchedElevator = dispatcher.dispatch(elevatorCall);

        assertThat(dispatchedElevator).isEqualTo(elevator2);
    }

    @Test
    @DisplayName("If there is no IDLE elevators, it should assign least busy elevator")
    void shouldAssignLeastBusyElevatorWhenNoIdleElevatorAvailable(){
        Elevator elevator1 = new Elevator();
        elevator1.setCurrentFloor(8);
        elevator1.setStatus(ElevatorStatus.MOVING);
        elevator1.setStopsQueue(new ArrayList<>(List.of(2,3)));
        elevator1.setDirection(Direction.UP);

        Elevator elevator2 = new Elevator();
        elevator2.setCurrentFloor(5);
        elevator2.setStatus(ElevatorStatus.MOVING);
        elevator2.setStopsQueue(new ArrayList<>(List.of(4,5,6,7)));
        elevator2.setDirection(Direction.UP);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(1);
        elevatorCall.setDirection(Direction.DOWN);

        when(elevatorRepository.findAll()).thenReturn(List.of(elevator1, elevator2));

        Elevator dispatchedElevator = dispatcher.dispatch(elevatorCall);

        assertThat(dispatchedElevator).isEqualTo(elevator1);

    }

    @DisplayName("If elevator is moving in opposite direction, it should assign different elevator")
    @Test
    void shouldNotAssignElevatorMovingInOppositeDirection(){
        Elevator elevator1 = new Elevator();
        elevator1.setCurrentFloor(5);
        elevator1.setStatus(ElevatorStatus.MOVING);
        elevator1.setStopsQueue(new ArrayList<>(List.of(7, 8)));
        elevator1.setDirection(Direction.UP);

        Elevator elevator2 = new Elevator();
        elevator2.setCurrentFloor(15);
        elevator2.setStatus(ElevatorStatus.IDLE);
        elevator2.setStopsQueue(new ArrayList<>());
        elevator2.setDirection(Direction.NONE);

        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.DOWN);

        when(elevatorRepository.findAll()).thenReturn(List.of(elevator1, elevator2));

        Elevator dispatchedElevator = dispatcher.dispatch(elevatorCall);

        assertThat(dispatchedElevator).isEqualTo(elevator2);

    }

    @DisplayName("If there are no elevators available, it should throw IllegalStateException")
    @Test
    void shouldThrowExceptionWhenNoElevatorsAvailable(){
        ElevatorCall elevatorCall = new ElevatorCall();
        elevatorCall.setFloor(3);
        elevatorCall.setDirection(Direction.UP);

        when(elevatorRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> dispatcher.dispatch(elevatorCall)).isInstanceOf(IllegalStateException.class);
    }
}
