package pl.marcelgiercarz.elevatorsystemapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorRequest {
    private int targetFloor;

    @NotNull
    private Long elevatorId;
}
