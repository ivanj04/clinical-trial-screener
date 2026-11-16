package com.ivanjiang.ctscreener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CtScreenerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context starts up correctly with embedded MongoDB
    }
}
