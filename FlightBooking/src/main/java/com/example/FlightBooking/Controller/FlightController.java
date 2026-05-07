package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.FlightDTO;
import com.example.FlightBooking.DTO.SearchDTO;
import com.example.FlightBooking.Service.FlightService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public FlightDTO addFlight(@Valid @RequestBody FlightDTO flightDTO) {
        return flightService.addFlight(flightDTO);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public Page<FlightDTO> getAllFlights(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return flightService.getAllFlights(pageable);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public FlightDTO getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public FlightDTO updateFlight(@PathVariable Long id, @Valid @RequestBody FlightDTO flightDTO) {
        return flightService.updateFlight(id, flightDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteFlight(@PathVariable Long id) {
        return flightService.deleteFlight(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/search")
    public Page<FlightDTO> searchFlights(@Valid @RequestBody SearchDTO searchDTO,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime"));
        return flightService.searchFlights(searchDTO, pageable);
    }
}
