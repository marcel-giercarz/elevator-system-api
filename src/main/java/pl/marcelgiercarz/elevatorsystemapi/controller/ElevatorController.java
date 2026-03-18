package pl.marcelgiercarz.elevatorsystemapi.controller;

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
public class ElevatorController {

    private final ElevatorService elevatorService;

    @GetMapping
    public ResponseEntity<List<Elevator>> getAllElevators(){
        return ResponseEntity.ok(elevatorService.getAllElevators());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Elevator> getElevatorById(@PathVariable Long id){
        return ResponseEntity.ok(elevatorService.getElevatorById(id));
    }

    @PostMapping
    public ResponseEntity<Elevator> addElevator(){
        return ResponseEntity.status(HttpStatus.CREATED).body(elevatorService.addElevator());
    }

    @PostMapping("/call")
    public ResponseEntity<ElevatorCall> callElevator(@Valid @RequestBody ElevatorCallRequest elevatorCallRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(elevatorService.callElevator(elevatorCallRequest.getFloor(), elevatorCallRequest.getDirection()));
    }

    @GetMapping("/calls")
    public ResponseEntity<List<ElevatorCall>> getAllElevatorCalls(){
        return ResponseEntity.ok(elevatorService.getAllElevatorCalls());
    }

    @PostMapping("/floor-request")
    public ResponseEntity<Elevator> floorRequest(@Valid @RequestBody FloorRequest floorRequest){
        return ResponseEntity.ok(elevatorService.floorRequest(floorRequest.getElevatorId(), floorRequest.getTargetFloor()));
    }

    @PostMapping("/step")
    public ResponseEntity<List<Elevator>> simulateStep(){
        return ResponseEntity.ok(elevatorService.simulateStepForAllElevators());
    }
}
