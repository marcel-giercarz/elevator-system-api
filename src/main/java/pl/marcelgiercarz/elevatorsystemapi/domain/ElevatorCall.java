package pl.marcelgiercarz.elevatorsystemapi.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.CallStatus;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "elevators_calls")
public class ElevatorCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int floor;

    @Nullable
    private Integer targetFloor;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Enumerated(EnumType.STRING)
    private CallStatus status;

    @ManyToOne
    private Elevator assignedElevator;
}
