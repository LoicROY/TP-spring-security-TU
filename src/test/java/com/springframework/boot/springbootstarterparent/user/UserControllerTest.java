package com.springframework.boot.springbootstarterparent.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;


    // Tester qu’on ne peut pas accéder à un user (Pas de role user)
    @Test
    public void getUserByUsernameTestKoNoAuthenticate() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/api/users/getThisUser")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Tester qu’on n’a pas trouver un user
    @Test
    public void getUserByUsernameTestKoUserNotFound() throws Exception {
        Mockito
                .when(userService.getUserByUsername(ArgumentMatchers.anyString()))
                .thenThrow(UserNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/api/users/getThisUser")
                                .with(SecurityMockMvcRequestPostProcessors.user("test").roles("USER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException)
                );
    }

    // Tester qu’on peut créer un user en remplissant les conditions de la méthode
    @Test
    public void createNewUserTestOk() throws Exception {
        Mockito
                .doNothing()
                .when(userService)
                .storeNewUser(ArgumentMatchers.isA(User.class));

        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"username\": \"test\", \"email\": \"test@yopmail.com\" }")
                                .with(SecurityMockMvcRequestPostProcessors.user("test").roles("USER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.containsString("test")));
    }

    // Tester qu’on ne peut pas créer un user sans remplir les conditions de la méthode
    @Test
    public void createNewUserTestKoRoleAdmin() throws Exception {
        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"username\": \"test\", \"email\": \"test@yopmail.com\" }")
                                .with(SecurityMockMvcRequestPostProcessors.user("test").roles("ADMIN"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // Tester qu’on peut supprimer un user en remplissant les conditions de la méthode
    @Test
    public void deleteUserTestOk() throws Exception {
        Mockito
                .when(userService.getUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(new User());

        Mockito
                .when(userService.deleteUser(ArgumentMatchers.any(User.class)))
                .thenReturn(true);

        this.mockMvc
                .perform(
                        delete("/api/users/deleteThisUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"username\": \"test\", \"email\": \"test@yopmail.com\" }")
                                .with(SecurityMockMvcRequestPostProcessors.user("test").roles("ADMIN"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

}
