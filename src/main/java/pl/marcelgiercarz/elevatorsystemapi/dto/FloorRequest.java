package pl.marcelgiercarz.elevatorsystemapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to send elevator to target floor")
public class FloorRequest {
    @Schema(description = "Target floor number", example = "7")
    private int targetFloor;

    @Schema(description = "ID of elevator to send to target floor", example = "2")
    @NotNull
    private Long elevatorId;
}
