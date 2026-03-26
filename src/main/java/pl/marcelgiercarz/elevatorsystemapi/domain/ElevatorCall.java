package pl.marcelgiercarz.elevatorsystemapi.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Elevator call request, represents user request to call elevator to specific floor")
public class ElevatorCall {
    @Schema(description = "Unique call ID (auto-generated)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Floor where elevator should come", example = "2")
    private int floor;

    @Schema(description = "Target floor inside elevator", example = "10")
    @Nullable
    private Integer targetFloor;

    @Schema(description = "Call direction from pickup floor", example = "UP")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Schema(description = "Current status of the call", example = "PENDING", allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"})
    @Enumerated(EnumType.STRING)
    private CallStatus status;

    @Schema(description = "Elevator assigned to handle this call" )
    @ManyToOne
    private Elevator assignedElevator;
}
