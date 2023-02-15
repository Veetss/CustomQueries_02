package co.develhope.customqueries2.controllers;

import co.develhope.customqueries2.entities.Flight;
import co.develhope.customqueries2.entities.FlightStatus;
import co.develhope.customqueries2.repositories.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @GetMapping("/provision")
    public ResponseEntity<String> provisionFlights(@RequestParam(value = "n", defaultValue = "100") int n) {
        Random random = new Random();
        List<Flight> flights = IntStream.range(0, n)
                .mapToObj(i -> new Flight(
                        null,
                        String.valueOf(random.nextInt(10000)),
                        String.valueOf(random.nextInt(10000)),
                        LocalDateTime.now().plusDays(random.nextInt(365)),
                        LocalDateTime.now().plusDays(random.nextInt(365)),
                        FlightStatus.values()[random.nextInt(3)]
                )).collect(Collectors.toList());
        flightRepository.saveAll(flights);
        return ResponseEntity.ok("Flights provisioned");
    }

    @GetMapping("")
    public ResponseEntity<Page<Flight>> getAllFlights(Pageable pageable) {
        return ResponseEntity.ok(flightRepository.findAll(pageable));
    }

    @GetMapping("/ontime")
    public ResponseEntity<List<Flight>> getOntimeFlights() {
        return ResponseEntity.ok(flightRepository.findAll().stream()
                .filter(flight -> flight.getStatus().equals(FlightStatus.ONTIME))
                .collect(Collectors.toList()));
    }

    @GetMapping("/custom")
    public ResponseEntity<List<Flight>> getFlightsByStatus(@RequestParam("p1") FlightStatus p1,
                                                           @RequestParam("p2") FlightStatus p2) {
        return ResponseEntity.ok(flightRepository.findByStatusIn(Arrays.asList(p1, p2)));
    }
}