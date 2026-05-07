package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.FlightDTO;
import com.example.FlightBooking.DTO.SearchDTO;
import com.example.FlightBooking.Exception.BadRequestException;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.Flight;
import com.example.FlightBooking.Repository.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final MapperService mapperService;

    public FlightService(FlightRepository flightRepository, MapperService mapperService) {
        this.flightRepository = flightRepository;
        this.mapperService = mapperService;
    }

    public FlightDTO addFlight(FlightDTO flightDTO) {
        if (flightDTO.getAvailableSeats() > flightDTO.getTotalSeats()) {
            throw new BadRequestException("Available seats cannot be greater than total seats");
        }

        Flight flight = new Flight();
        copyToFlight(flightDTO, flight);
        return mapperService.toFlightDTO(flightRepository.save(flight));
    }

    public Page<FlightDTO> getAllFlights(Pageable pageable) {
        return flightRepository.findAll(pageable).map(mapperService::toFlightDTO);
    }

    public FlightDTO getFlightById(Long id) {
        return mapperService.toFlightDTO(findFlightEntity(id));
    }

    public FlightDTO updateFlight(Long id, FlightDTO flightDTO) {
        Flight flight = findFlightEntity(id);
        copyToFlight(flightDTO, flight);
        return mapperService.toFlightDTO(flightRepository.save(flight));
    }

    public String deleteFlight(Long id) {
        Flight flight = findFlightEntity(id);
        flightRepository.delete(flight);
        return "Flight deleted successfully";
    }

    public Page<FlightDTO> searchFlights(SearchDTO searchDTO, Pageable pageable) {
        LocalDateTime start = searchDTO.getDate().atStartOfDay();
        LocalDateTime end = searchDTO.getDate().plusDays(1).atStartOfDay();
        return flightRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(searchDTO.getSource(), searchDTO.getDestination(), start, end, pageable)
                .map(mapperService::toFlightDTO);
    }

    public Flight findFlightEntity(Long id) {
        return flightRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
    }

    private void copyToFlight(FlightDTO dto, Flight flight) {
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setSource(dto.getSource());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setTotalSeats(dto.getTotalSeats());
        flight.setAvailableSeats(dto.getAvailableSeats());
        flight.setBaseFare(dto.getBaseFare());
    }
}
