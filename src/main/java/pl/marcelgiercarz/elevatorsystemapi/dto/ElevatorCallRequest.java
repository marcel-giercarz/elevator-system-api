package pl.marcelgiercarz.elevatorsystemapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to call elevator to specific floor with direction")
public class ElevatorCallRequest {
    @Schema(description = "Floor number to call elevator", example = "3")
    private int floor;

    @Schema(description = "Direction of travel from pickup floor", allowableValues = {"UP", "DOWN"})
    @NotNull
    private Direction direction;
}
