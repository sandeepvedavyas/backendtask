package com.sandeep.backendtask;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandeep.backendtask.controller.UserController;
import com.sandeep.backendtask.dto.UserDto;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.repository.UserRepository;
import com.sandeep.backendtask.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = { "/schema.sql","/data.sql" }, config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public class UserControllerTests {

	@Mock
	UserRepository userRepository;
	
	@Autowired
	private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;
    
    @Autowired
	private ModelMapper modelMapper;
    
    @Before
    public void init(){
    }

    // =========================================== Get All Users ==========================================

    @Test
    public void test_get_all_success() throws Exception {
		List<User> users = new ArrayList<>();
		users.add(new User(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com"));
		users.add(new User(2L, "sunayana", "password", "sunayana", "nanjegowda", "testuser1@gmail.com"));
		users.add(new User(3L, "kishor", "password", "kishor", "vedavyas", "testuser1@gmail.com"));

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("sandeep")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("sunayana")));

        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void test_get_all_success_no_content() throws Exception {

        when(userService.getAll()).thenReturn(null);

        mockMvc.perform(get("/users"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }

    // =========================================== Get UserDto By ID =========================================

    @Test
    public void test_get_by_id_success() throws Exception {
        UserDto userDto = new UserDto(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
        User user = convertToEntity(userDto);
        when(userService.getById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("sandeep")));

        verify(userService, times(2)).getById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_get_by_id_fail_404_not_found() throws Exception {
        when(userService.getById(4l)).thenReturn(null);

        mockMvc.perform(get("/users/{id}", 4))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getById(4l);
        verifyNoMoreInteractions(userService);
    }

    // =========================================== Create New UserDto ========================================

    @Test
    public void test_create_user_success() throws Exception {
    	 UserDto userDto = new UserDto("sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
        when(userService.exists(convertToEntity(userDto))).thenReturn(false);
       when(userService.save(convertToEntity(userDto))).thenReturn(convertToEntity(userDto));

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("/users/")));
        verify(userService, times(1)).save(convertToEntity(userDto));
    }

    @Test
    public void test_create_user_fail_404_not_found() throws Exception {
    	
    	UserDto userDto = new UserDto("sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
        when(userService.exists(convertToEntity(userDto))).thenReturn(true);
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).exists(convertToEntity(userDto));
    }

    // =========================================== Update Existing UserDto ===================================

    @Test
    public void test_update_user_success() throws Exception {
    	 UserDto userDto = new UserDto(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

        when(userService.exists(convertToEntity(userDto))).thenReturn(false);
        User user = convertToEntity(userDto);
        when(userService.save(convertToEntity(userDto))).thenReturn(user);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("/users/1")));
        verify(userService, times(1)).save(user);
    }

    @Test
    public void test_update_user_fail_404_not_found() throws Exception {
    	UserDto userDto = new UserDto(1L,"sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

        when(userService.getById(userDto.getId())).thenReturn(null);

        mockMvc.perform(
                put("/users/{id}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getById(userDto.getId());
        verifyNoMoreInteractions(userService);
    }

    // =========================================== Delete UserDto ============================================

    @Test
    public void test_delete_user_success() throws Exception {
    	UserDto userDto = new UserDto(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
    	User user = convertToEntity(userDto);
    	 when(userService.getById(userDto.getId())).thenReturn(Optional.of(user));
        doNothing().when(userService).remove(userDto.getId());

        mockMvc.perform(
                delete("/users/{id}", userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, times(2)).getById(userDto.getId());
        verify(userService, times(1)).remove(userDto.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_delete_user_fail_404_not_found() throws Exception {
    	UserDto userDto = new UserDto(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

        when(userService.getById(userDto.getId())).thenReturn(null);

        mockMvc.perform(
                delete("/users/{id}", userDto.getId()))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getById(userDto.getId());
        verifyNoMoreInteractions(userService);
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private UserDto convertToDto(User user) {
		UserDto userDto = modelMapper.map(user, UserDto.class);
		return userDto;
	}

	private User convertToEntity(UserDto userDto) throws ParseException {
		User user = modelMapper.map(userDto, User.class);

		if (userDto.getId() != null) {
			Optional<User> existingUser = userService.getById(userDto.getId());
			if (existingUser.isPresent()) {
				user.setId(userDto.getId());
				user.setFirstName(userDto.getFirstName());
				user.setLastName(userDto.getLastName());
				user.setNickName(userDto.getNickName());
				user.setPassword(userDto.getPassword());

			}
		}
		return user;
	}
}
