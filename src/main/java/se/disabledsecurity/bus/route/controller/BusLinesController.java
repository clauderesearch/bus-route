package se.disabledsecurity.bus.route.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.disabledsecurity.bus.route.model.internal.FrontEndModel;
import se.disabledsecurity.bus.route.service.CommuterInformationService;

import java.util.List;

@RestController
@RequestMapping(path = "/")
@Tag(name = "Bus Lines", description = "Operations for retrieving bus line information")
public class BusLinesController {

	private final CommuterInformationService commuterInformationService;

	public BusLinesController(CommuterInformationService commuterInformationService) {
		this.commuterInformationService = commuterInformationService;
	}

	@Operation(
		summary = "Get top 10 bus lines with most stops",
		description = "Returns the details of top 10 bus lines in Stockholm area with most bus stops"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved bus lines",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = FrontEndModel.class)
			)
		),
		@ApiResponse(
			responseCode = "503", 
			description = "External API unavailable",
			content = @Content
		)
	})
	@GetMapping(value = "lines")
	public List<FrontEndModel> findLinesWithMostStops() {
		return commuterInformationService
				.findBusLinesWithMostStops(10);
	}
}
