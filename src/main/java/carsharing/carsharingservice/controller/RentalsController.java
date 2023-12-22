package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.rental.ActualReturnDateDto;
import carsharing.carsharingservice.dto.rental.CreateRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalActiveStatusDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rentals")
@Tag(name = "Rentals management", description = "Endpoints for CRUD operations with rentals")
public class RentalsController {
    private final RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Add a new rental", description = "Adds a new car rental")
    public RentalResponseDto placeNewRental(
            Authentication authentication,
            @RequestBody @Valid CreateRentalRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return rentalService.addNewRental(user.getId(), requestDto);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get rentals by user id and rental status",
            description = "Using this endpoint you can get a list of rentals by user's id "
                    + "and status of rentals")
    public List<RentalResponseDto> getRentalByUserIdAndStatus(
            @Valid RentalSearchParametersDto parametersDto) {
        return rentalService.getRentalsByParameters(parametersDto);
    }

    @GetMapping(value = "/status")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get rentals by rental status",
            description = "Using this endpoint you can get a list of all rentals by "
                    + "status of rentals including users' details")
    public List<RentalResponseFullInfoDto> getRentalsByStatus(
            @Valid RentalActiveStatusDto requestDto) {
        return rentalService.getRentalsByStatus(requestDto);
    }

    @GetMapping(value = "/{rentalId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get rental by id", description = "Returns specific rental by id")
    public RentalResponseDto getRentalById(Authentication authentication,
                                           @PathVariable Long rentalId) {
        User user = (User) authentication.getPrincipal();
        return rentalService.getRentalById(user.getId(), rentalId);
    }

    @PostMapping(value = "/return")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Set actual return date", description = "Using this endpoint "
            + "the customer can set actual return date")
    public RentalResponseDto setActualReturnDate(Authentication authentication,
                                                 @Valid ActualReturnDateDto returnDateDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.setActualReturnDate(user.getId(), returnDateDto);
    }
}
