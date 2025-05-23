package se.disabledsecurity.bus.route.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.disabledsecurity.bus.route.clients.TrafikLabClient;
import se.disabledsecurity.bus.route.model.external.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BusLinesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafikLabClient trafikLabClient;

    @Test
    void getLinesEndpoint_shouldReturnBusLinesSuccessfully() throws Exception {
        // Arrange
        List<Route> routes = List.of(
            new Route(1, "1", 1001, "2023-01-01", "2023-01-01"),
            new Route(1, "1", 1002, "2023-01-01", "2023-01-01"),
            new Route(2, "1", 2001, "2023-01-01", "2023-01-01")
        );

        List<Line> lines = List.of(
            new Line(1, "Line 1", "BUS", "BUS", "2023-01-01", "2023-01-01"),
            new Line(2, "Line 2", "BUS", "BUS", "2023-01-01", "2023-01-01")
        );

        List<StopPoint> stopPoints = List.of(
            new StopPoint(1001, "Central Station", 1001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(1002, "Main Square", 1002, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01"),
            new StopPoint(2001, "Airport", 2001, "123", "456", "A", "BUSTERM", "2023-01-01", "2023-01-01")
        );

        when(trafikLabClient.getAllBusRoutes()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "Route", routes))
        );
        when(trafikLabClient.getAllBuses()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "Line", lines))
        );
        when(trafikLabClient.getAllBusStops()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "StopPoint", stopPoints))
        );

        // Act & Assert
        mockMvc.perform(get("/lines"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].lineNumber").value(1))
            .andExpect(jsonPath("$[0].stops").isArray())
            .andExpect(jsonPath("$[0].stops[0]").value("Central Station"))
            .andExpect(jsonPath("$[0].stops[1]").value("Main Square"));
    }

    @Test
    void getLinesEndpoint_shouldHandleEmptyResponse() throws Exception {
        // Arrange
        when(trafikLabClient.getAllBusRoutes()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "Route", List.of()))
        );
        when(trafikLabClient.getAllBuses()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "Line", List.of()))
        );
        when(trafikLabClient.getAllBusStops()).thenReturn(
            new ApiBaseModel<>(200, "OK", 100, new ApiData<>("1.0", "StopPoint", List.of()))
        );

        // Act & Assert
        mockMvc.perform(get("/lines"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}