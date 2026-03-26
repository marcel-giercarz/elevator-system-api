package pl.marcelgiercarz.elevatorsystemapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.marcelgiercarz.elevatorsystemapi.domain.Elevator;
import pl.marcelgiercarz.elevatorsystemapi.domain.ElevatorCall;
import pl.marcelgiercarz.elevatorsystemapi.dto.ElevatorCallRequest;
import pl.marcelgiercarz.elevatorsystemapi.dto.FloorRequest;
import pl.marcelgiercarz.elevatorsystemapi.service.ElevatorService;

import java.util.List;

@RestController
@RequestMapping("/api/elevators")
@RequiredArgsConstructor
@Tag(name = "Elevator API", description = "Elevators management system")
public class ElevatorController {

    private final ElevatorService elevatorService;

    @Operation(
            summary = "Get all elevators",
            description = "Returns a list of all elevators with current status"
    )
    @ApiResponse(responseCode = "200", description = "Elevators list retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Elevator>> getAllElevators(){
        return ResponseEntity.ok(elevatorService.getAllElevators());
    }

    @Operation(
            summary = "Get elevator by ID",
            description = "Returns detailed information about elevator with given ID"
    )
    @ApiResponse(responseCode = "200", description = "Elevator found")
    @ApiResponse(responseCode = "404", description = "Elevator with given ID not found")
    @GetMapping("/{id}")
    public ResponseEntity<Elevator> getElevatorById(@Parameter(description = "Elevator ID") @PathVariable Long id){
        return ResponseEntity.ok(elevatorService.getElevatorById(id));
    }

    @Operation(
            summary = "Add new elevator",
            description = "Creates new elevator with default settings"
    )
    @ApiResponse(responseCode = "201", description = "Elevator created successfully")
    @PostMapping
    public ResponseEntity<Elevator> addElevator(){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(elevatorService.addElevator());
    }

    @Operation(
            summary = "Call elevator to floor",
            description = "Call elevator to specific floor with direction (UP/DOWN)"
    )
    @ApiResponse(responseCode = "201", description = "Call registered")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "503", description = "No elevators available")
    @PostMapping("/call")
    public ResponseEntity<ElevatorCall> callElevator(@Valid @RequestBody ElevatorCallRequest elevatorCallRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(elevatorService.callElevator(elevatorCallRequest.getFloor(), elevatorCallRequest.getDirection()));
    }

    @Operation(
            summary = "Get all elevator calls history",
            description = "Returns all registered elevator calls"
    )
    @ApiResponse(responseCode = "200", description = "Calls list retrieved")
    @GetMapping("/calls")
    public ResponseEntity<List<ElevatorCall>> getAllElevatorCalls(){
        return ResponseEntity.ok(elevatorService.getAllElevatorCalls());
    }

    @Operation(
            summary = "Request target floor",
            description = "Send floor request to specific elevator to go to target floor"
    )
    @ApiResponse(responseCode = "200", description = "Floor request registered")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Elevator not found")
    @PostMapping("/floor-request")
    public ResponseEntity<Elevator> floorRequest(@Valid @RequestBody FloorRequest floorRequest){
        return ResponseEntity.ok(elevatorService.floorRequest(floorRequest.getElevatorId(), floorRequest.getTargetFloor()));
    }

    @Operation(
            summary = "Simulate one step",
            description = "Simulates one step for all elevators (simulation)"
    )
    @ApiResponse(responseCode = "200", description = "Simulation step executed")
    @PostMapping("/step")
    public ResponseEntity<List<Elevator>> simulateStep(){
        return ResponseEntity.ok(elevatorService.simulateStepForAllElevators());
    }
}
