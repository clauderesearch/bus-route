package se.disabledsecurity.bus.route;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.disabledsecurity.bus.route.clients.TrafikLabClient;

@SpringBootTest
class BusRouteApplicationTests {

	@MockBean
	private TrafikLabClient trafikLabClient;

	@Test
	void contextLoads() {
	}

}
