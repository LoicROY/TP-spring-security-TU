package com.springframework.boot.springbootstarterparent.dashboard;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DashboardService dashboardService;

    @Test
    public void shouldReturnViewWithPrefilledData() throws Exception {
        Mockito.when(dashboardService.getAnalyticsGraphData()).thenReturn(new Integer[]{ 13, 42 });

        mockMvc
                .perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", "Duke"))
                .andExpect(MockMvcResultMatchers.model().attribute("analyticsGraph", Matchers.arrayContaining(13, 42)))
                .andExpect(MockMvcResultMatchers.model().attributeExists("quickNote"));
    }
}
