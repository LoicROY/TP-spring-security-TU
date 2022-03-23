package com.springframework.boot.springbootstarterparent.task;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;

    @Test
    public void shouldRejectCreatingReviewsWhenUserIsAnonymous() throws Exception {
        this.mockMvc
                .perform(
                        post("/api/tasks/demo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"taskTitle\": \"Learn MockMvc\" }")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("admin")
    public void shouldRejectCreatingReviewsWhenUserIsOk() throws Exception {
        Mockito
                .when(taskService.createTask(ArgumentMatchers.anyString()))
                .thenReturn(82L);

        this.mockMvc
                .perform(
                        post("/api/tasks/demo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"taskTitle\": \"Learn MockMvc\" }")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.containsString("82")));
    }

    @Test
    @WithMockUser("user")
    public void shouldRejectDeletingReviewsWhenUserLacksAdminRole() throws Exception {
        this.mockMvc
                .perform(
                        delete("/api/tasks/demo/42")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldAllowDeletingReviewsWhenUserIsAdmin() throws Exception {
        this.mockMvc
                .perform(
                        delete("/api/tasks/demo/42")
                                .with(SecurityMockMvcRequestPostProcessors.user("test").roles("ADMIN"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk());

        verify(taskService).deleteTask(42L);
    }

}
