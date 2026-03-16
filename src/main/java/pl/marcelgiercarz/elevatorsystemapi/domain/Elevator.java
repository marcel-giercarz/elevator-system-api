package pl.marcelgiercarz.elevatorsystemapi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.ElevatorStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "elevators")
public class Elevator {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private int currentFloor;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Enumerated(EnumType.STRING)
    private ElevatorStatus status;

    @ElementCollection
    private List<Integer> stopsQueue;
}
