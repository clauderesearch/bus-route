
package se.disabledsecurity.bus.route.service;

import org.springframework.stereotype.Service;
import se.disabledsecurity.bus.route.clients.TrafikLabClient;
import se.disabledsecurity.bus.route.exception.BusLineNotFoundException;
import se.disabledsecurity.bus.route.exception.StopPointNotFoundException;
import se.disabledsecurity.bus.route.model.external.Line;
import se.disabledsecurity.bus.route.model.external.Route;
import se.disabledsecurity.bus.route.model.external.StopPoint;
import se.disabledsecurity.bus.route.model.internal.FrontEndModel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SwedishCommuterInformationService implements CommuterInformationService {

	private final TrafikLabClient trafikLabClient;

	private final Comparator<Map.Entry<Integer, List<Route>>> byNumberOfStoppingPoints = Map.Entry.comparingByValue(
			Comparator.comparingInt(List::size));

	public SwedishCommuterInformationService(TrafikLabClient trafikLabClient) {
		this.trafikLabClient = trafikLabClient;
	}


	@Override
	public List<FrontEndModel> findBusLinesWithMostStops(int numberOfBusLines) {
		var lines= findAllRouteByLine()
				.entrySet()
				.stream()
				.sorted(byNumberOfStoppingPoints.reversed())
				.limit(numberOfBusLines)
				.map(Map.Entry::getKey)
				.map(this::getLineDetailsFor)
				.toList();

		return lines
				.stream()
				.map(line -> new FrontEndModel(line.lineNumber(),
											   findStopsOnRoute(line.lineNumber())
													   .stream()
													   .map(StopPoint::stopPointName)
													   .toList()))
				.toList();

	}

	private Map<Integer, List<Route>> findAllRouteByLine() {
		return trafikLabClient
				.getAllBusRoutes()
				.responseData()
				.result()
				.stream()
				.collect(Collectors.groupingBy(Route::lineNumber));
	}

	private Line getLineDetailsFor(int lineNumber) {
		return trafikLabClient
				.getAllBuses()
				.responseData()
				.result()
				.stream()
				.filter(line -> line.lineNumber() == lineNumber)
				.findFirst()
				.orElse(null);
	}

	public List<StopPoint> findStopsOnRoute(int lineNumber) {
		var allRouteByLine = findAllRouteByLine();

		List<Route> routes =
				Optional
						.ofNullable(allRouteByLine.get(lineNumber))
						.orElseThrow(() -> new BusLineNotFoundException("No bus line exists for number: " + lineNumber));

		return routes.stream()
											 .map(Route::journeyPatternPointNumber)
											 .map(this::getStopPointDetailsFor)
											 .toList();
	}

	private StopPoint getStopPointDetailsFor(int pointNumber) {
		return trafikLabClient.getAllBusStops().responseData().result()
							  .stream()
							  .filter(stopPoint -> stopPoint.stopPointNumber() == pointNumber)
							  .findFirst()
							  .orElseThrow(() -> new StopPointNotFoundException("No stop point details could be found for point number: " + pointNumber));
	}
}
