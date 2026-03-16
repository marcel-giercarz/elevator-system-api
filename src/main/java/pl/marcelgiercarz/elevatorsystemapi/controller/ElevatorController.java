package pl.marcelgiercarz.elevatorsystemapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<Elevator> getAllElevators(){
        return elevatorService.getAllElevators();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Elevator> getElevatorById(@PathVariable Long id){
        return elevatorService.getElevatorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Elevator addElevator(){
        return elevatorService.addElevator();
    }

    @PostMapping("/call")
    public ElevatorCall callElevator(@Valid @RequestBody ElevatorCallRequest elevatorCallRequest){
        return elevatorService.callElevator(elevatorCallRequest.getFloor(), elevatorCallRequest.getDirection());
    }

    @GetMapping("/calls")
    public List<ElevatorCall> getAllElevatorCalls(){
        return elevatorService.getAllElevatorCalls();
    }

    @PostMapping("/floor-request")
    public ResponseEntity<Elevator> floorRequest(@Valid @RequestBody FloorRequest floorRequest){
        return ResponseEntity.ok(elevatorService.floorRequest(floorRequest.getElevatorId(), floorRequest.getTargetFloor()));
    }

    @PostMapping("/step")
    public List<Elevator> simulateStep(){
        return elevatorService.simulateStepForAllElevators();
    }
}
