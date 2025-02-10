package com.example.user;

import com.example.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenAuthentificationIT {
    public static final String SIGNUP = "/api/users/signup";
    public static final String LOGIN = "/api/users/login";
    public static final String GET_DETAILS = "/api/users/johndoe@example.com";
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Test
    public void testUserSignup() throws Exception {
        // Prepare user registration payload
        MockHttpServletRequestBuilder signupRequest = MockMvcRequestBuilders
                .post(SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"johnDoe\",\"password\":\"password123\",\"email\":\"johndoe@example.com\"}");

        //register user
        mockMvc.perform(signupRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("johnDoe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("johndoe@example.com"));

        //test bad password login
        MockHttpServletRequestBuilder badLoginRequest = MockMvcRequestBuilders
                .post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"abadpassword\",\"email\":\"johndoe@example.com\"}");
        MockHttpServletResponse response = mockMvc.perform(badLoginRequest).andExpect(status().isOk()).andReturn().getResponse();
        assertEquals(response.getContentAsString(), "Invalid user");

        //correctly login user
        MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders
                .post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"password123\",\"email\":\"johndoe@example.com\"}");

        response = mockMvc.perform(loginRequest).andExpect(status().isOk()).andReturn().getResponse();
        String jwtToken = response.getContentAsString();
        assertNotNull(jwtToken);

        //get details with jwt token
        MockHttpServletRequestBuilder detailsRequest = MockMvcRequestBuilders
                .get(GET_DETAILS)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(detailsRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("johnDoe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("johndoe@example.com"));

    }

}
