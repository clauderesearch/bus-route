package se.disabledsecurity.bus.route.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.disabledsecurity.bus.route.model.internal.FrontEndModel;
import se.disabledsecurity.bus.route.service.CommuterInformationService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusLinesController.class)
class BusLinesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommuterInformationService commuterInformationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findLinesWithMostStops_shouldReturnBusLines() throws Exception {
        // Arrange
        List<FrontEndModel> mockResponse = List.of(
            new FrontEndModel(1, List.of("Stop A", "Stop B", "Stop C")),
            new FrontEndModel(2, List.of("Stop D", "Stop E"))
        );

        when(commuterInformationService.findBusLinesWithMostStops(10))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/lines"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].lineNumber").value(1))
            .andExpect(jsonPath("$[0].stops").isArray())
            .andExpect(jsonPath("$[0].stops.length()").value(3))
            .andExpect(jsonPath("$[0].stops[0]").value("Stop A"))
            .andExpect(jsonPath("$[1].lineNumber").value(2))
            .andExpect(jsonPath("$[1].stops.length()").value(2));
    }

    @Test
    void findLinesWithMostStops_shouldReturnEmptyListWhenNoData() throws Exception {
        // Arrange
        when(commuterInformationService.findBusLinesWithMostStops(10))
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/lines"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}