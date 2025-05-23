package se.disabledsecurity.bus.route.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.disabledsecurity.bus.route.clients.TrafikLabClient;
import se.disabledsecurity.bus.route.exception.BusLineNotFoundException;
import se.disabledsecurity.bus.route.exception.StopPointNotFoundException;
import se.disabledsecurity.bus.route.model.external.*;
import se.disabledsecurity.bus.route.model.internal.FrontEndModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwedishCommuterInformationServiceTest {

    @Mock
    private TrafikLabClient trafikLabClient;

    private SwedishCommuterInformationService service;

    @BeforeEach
    void setUp() {
        service = new SwedishCommuterInformationService(trafikLabClient);
    }

    @Test
    void findBusLinesWithMostStops_shouldReturnTopBusLines() {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01"),
            new Route(1, "1", 1002, "2023-01-01", "2023-01-01"),
            new Route(1, "1", 1003, "2023-01-01", "2023-01-01"),
            new Route(2, "1", 2001, "2023-01-01", "2023-01-01"),
            new Route(2, "1", 2002, "2023-01-01", "2023-01-01")
        );

        List<Line> lines = List.of(
            new Line(1, "Line 1", "BUS", "BUS", "2023-01-01", "2023-01-01"),
            new Line(2, "Line 2", "BUS", "BUS", "2023-01-01", "2023-01-01")
        );

        List<StopPoint> stopPoints = List.of(
            new StopPoint(1001, "Stop A", 1001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(1002, "Stop B", 1002, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(1003, "Stop C", 1003, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(2001, "Stop D", 2001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(2002, "Stop E", 2002, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01")
        );

        ApiBaseModel<Route> routeResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Route", routes)
        );
        ApiBaseModel<Line> lineResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Line", lines)
        );
        ApiBaseModel<StopPoint> stopResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "StopPoint", stopPoints)
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(routeResponse);
        when(trafikLabClient.getAllBuses()).thenReturn(lineResponse);
        when(trafikLabClient.getAllBusStops()).thenReturn(stopResponse);

        // Act
        List<FrontEndModel> result = service.findBusLinesWithMostStops(2);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).lineNumber()).isEqualTo(1);
        assertThat(result.get(0).stops()).containsExactly("Stop A", "Stop B", "Stop C");
        assertThat(result.get(1).lineNumber()).isEqualTo(2);
        assertThat(result.get(1).stops()).containsExactly("Stop D", "Stop E");
    }

    @Test
    void findBusLinesWithMostStops_shouldLimitResults() {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01"),
            new Route(2, "1", 2001, "2023-01-01", "2023-01-01"),
            new Route(3, "1", 3001, "2023-01-01", "2023-01-01")
        );

        List<Line> lines = List.of(
            new Line(1, "Line 1", "BUS", "BUS", "2023-01-01", "2023-01-01"),
            new Line(2, "Line 2", "BUS", "BUS", "2023-01-01", "2023-01-01"),
            new Line(3, "Line 3", "BUS", "BUS", "2023-01-01", "2023-01-01")
        );

        List<StopPoint> stopPoints = List.of(
            new StopPoint(1001, "Stop A", 1001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(2001, "Stop B", 2001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(3001, "Stop C", 3001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01")
        );

        ApiBaseModel<Route> routeResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Route", routes)
        );
        ApiBaseModel<Line> lineResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Line", lines)
        );
        ApiBaseModel<StopPoint> stopResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "StopPoint", stopPoints)
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(routeResponse);
        when(trafikLabClient.getAllBuses()).thenReturn(lineResponse);
        when(trafikLabClient.getAllBusStops()).thenReturn(stopResponse);

        // Act
        List<FrontEndModel> result = service.findBusLinesWithMostStops(1);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void findStopsOnRoute_shouldReturnStopsForValidLine() {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01"),
            new Route(1, "1", 1002, "2023-01-01", "2023-01-01")
        );

        List<StopPoint> stopPoints = List.of(
            new StopPoint(1001, "Stop A", 1001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(1002, "Stop B", 1002, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01")
        );

        ApiBaseModel<Route> routeResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Route", routes)
        );
        ApiBaseModel<StopPoint> stopResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "StopPoint", stopPoints)
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(routeResponse);
        when(trafikLabClient.getAllBusStops()).thenReturn(stopResponse);

        // Act
        List<StopPoint> result = service.findStopsOnRoute(1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).stopPointName()).isEqualTo("Stop A");
        assertThat(result.get(1).stopPointName()).isEqualTo("Stop B");
    }

    @Test
    void findStopsOnRoute_shouldThrowExceptionForInvalidLine() {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01")
        );

        ApiBaseModel<Route> routeResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Route", routes)
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(routeResponse);

        // Act & Assert
        assertThatThrownBy(() -> service.findStopsOnRoute(999))
            .isInstanceOf(BusLineNotFoundException.class)
            .hasMessage("No bus line exists for number: 999");
    }

    @Test
    void findStopsOnRoute_shouldThrowExceptionForMissingStopPoint() {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01")
        );

        List<StopPoint> stopPoints = List.of(); // Empty list

        ApiBaseModel<Route> routeResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "Route", routes)
        );
        ApiBaseModel<StopPoint> stopResponse = new ApiBaseModel<>(
            200, "OK", 100, new ApiData<>("1.0", "StopPoint", stopPoints)
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(routeResponse);
        when(trafikLabClient.getAllBusStops()).thenReturn(stopResponse);

        // Act & Assert
        assertThatThrownBy(() -> service.findStopsOnRoute(1))
            .isInstanceOf(StopPointNotFoundException.class)
            .hasMessage("No stop point details could be found for point number: 1001");
    }
}