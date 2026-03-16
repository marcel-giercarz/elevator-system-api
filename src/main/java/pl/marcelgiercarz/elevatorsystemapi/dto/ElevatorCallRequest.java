package pl.marcelgiercarz.elevatorsystemapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.marcelgiercarz.elevatorsystemapi.domain.enums.Direction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorCallRequest {
    private int floor;

    @NotNull
    private Direction direction;
}
