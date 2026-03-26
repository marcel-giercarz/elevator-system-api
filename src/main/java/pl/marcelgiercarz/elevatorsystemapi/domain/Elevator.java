package pl.marcelgiercarz.elevatorsystemapi.domain;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Elevator object")
public class Elevator {

    @Schema(description = "Unique elevator ID (auto-generated)", example = "1")
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Current floor position", example = "0")
    private int currentFloor;

    @Schema(description = "Current movement direction", example = "UP", allowableValues = {"UP", "DOWN", "NONE"})
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Schema(description = "Operational status", example = "MOVING", allowableValues = {"IDLE", "MOVING"})
    @Enumerated(EnumType.STRING)
    private ElevatorStatus status;

    @Schema(description = "Queue of upcoming stops/floors (ordered)", example = "[3, 7, 10]")
    @ArraySchema(schema = @Schema(implementation = Integer.class, description = "Target floor numbers"))
    @ElementCollection
    private List<Integer> stopsQueue;
}
