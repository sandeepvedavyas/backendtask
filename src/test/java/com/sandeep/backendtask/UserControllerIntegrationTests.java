package com.sandeep.backendtask;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {


    @LocalServerPort
    private int port;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private TestRestTemplate testRestTemplate;
    // =========================================== Get All Users ==========================================

    @Test
    public void test_get_all_success() throws DuplicateUserNameException{
        ResponseEntity<User[]> response = testRestTemplate.getForEntity("http://localhost:" + port + "/users", User[].class);
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    // =========================================== Get User By ID =========================================

    @Test
    public void test_get_by_id_success(){
        ResponseEntity<User> response = testRestTemplate.getForEntity("http://localhost:" + port + "/users" + "/1", User.class);
        User user = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    // =========================================== Create New User ========================================

    @Test
    public void test_create_new_user_success(){
    	User user = new User(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
        URI location = testRestTemplate.postForLocation("http://localhost:" + port + "/users", user, User.class);
        assertThat(location, notNullValue());
    }


    // =========================================== Update Existing User ===================================

    @Test
    public void test_update_user_success(){
    	User user = new User(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
        testRestTemplate.put("http://localhost:" + port + "/users" + "/" + user.getId(), user);
    }

    private User getLastUser(){
        ResponseEntity<User[]> response = testRestTemplate.getForEntity("http://localhost:" + port + "/users", User[].class);
        User[] users = response.getBody();
        return users[users.length - 1];
    }
}
