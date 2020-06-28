package com.sandeep.backendtask;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import com.sandeep.backendtask.dto.UserDto;
import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.exception.UserNotFoundException;
import com.sandeep.backendtask.model.Message;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.repository.MessageRepository;
import com.sandeep.backendtask.service.MessageService;
import com.sandeep.backendtask.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = { "/schema.sql","/data.sql" }, config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public class MessageControllerTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	MessageRepository messageRepository;

	@MockBean
	private MessageService messageService;
	
	@MockBean
	private UserService userService;


	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_get_all_success() throws Exception {
		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		List<Message> messages = getMessages();
		
		when(messageService.getAll()).thenReturn(messages);
		
		mockMvc.perform(get("/messages")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].content", is("testmessage")))
        .andExpect(jsonPath("$[1].id", is(2))).andExpect(jsonPath("$[1].senderId", is(2))).andExpect(jsonPath("$[1].receiverId", is(3)))
        .andExpect(jsonPath("$[1].content", is("testmessage2")));
		assertEquals(4, messageService.getAll().size());
	}

	@Test
	public void test_get_all_success_empty_list() throws Exception {
		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		when(messageService.getAll()).thenReturn(null);
		
		mockMvc.perform(get("/messages")).andExpect(status().isNoContent());
				
		assertNull(messageService.getAll());
	}
	
	@Test
	public void test_get_all_success_empty_list_empty() throws Exception {
		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		when(messageService.getAll()).thenReturn(null);
		
		mockMvc.perform(get("/messages")).andExpect(status().isNoContent());
				
		assertNull(messageService.getAll());
	}
	
	@Test
	public void test_find_ByReceiverId_And_SenderId() throws Exception {

		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		
		List<Message> messages = new ArrayList<>();
		messages.add(new Message(1L, "testmessage", 1L,2L));
		
		when(messageService.getAllByReceiverIdAndSenderId(1L, 2L)).thenReturn(messages);
		
		mockMvc.perform(get("/messages/received?userId=1&senderId=2")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].content", is("testmessage")))
        .andExpect(jsonPath("$[0].senderId", is(1))).andExpect(jsonPath("$[0].receiverId", is(2)));
		assertEquals(1,messages.size());
	}
	
	@Test
	public void test_find_ByReceiverId_And_Null_SenderId_empty_result() throws Exception {
		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		when(messageService.getAllByReceiverIdAndSenderId(1L, null)).thenReturn(null);
		
		mockMvc.perform(get("/messages/received?userId=1&senderId=")).andExpect(status().isNoContent());
				
		assertNotNull((messageService.getAllByReceiverIdAndSenderId(1L, 2L)));
	}
	
	@Test
	public void test_find_ByReceiverId_And_SenderId_empty_result() throws Exception {
		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		when(messageService.getAllByReceiverIdAndSenderId(1L, 2L)).thenReturn(null);
		
		mockMvc.perform(get("/messages/received?userId=1&senderId=2")).andExpect(status().isNoContent());
				
		assertNull(messageService.getAllByReceiverIdAndSenderId(1L, 2L));
	}

	@Test
	public void test_find_ByReceiverId_NoValidSenderId() throws Exception {

		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		List<Message> messages = new ArrayList<>();
		messages.add(new Message(1L, "testmessage", 1L,2L));
		
		when(messageService.getAllByReceiverIdAndSenderId(1L, 2L)).thenReturn(messages);
		
		mockMvc.perform(get("/messages/received?userId=1&senderId=2")).andExpect(status().isOk())
		
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].content", is("testmessage")))
        .andExpect(jsonPath("$[0].senderId", is(1))).andExpect(jsonPath("$[0].receiverId", is(2)));
		assertEquals(1,messages.size());
	}

	

	@Test
	public void test_find_BySender_Id() throws Exception {

		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());
		List<Message> messages = new ArrayList<>();
		messages.add(new Message(1L, "testmessage1", 1L,2L));
		messages.add(new Message(2L, "testmessage2", 1L,2L));
		
		when(messageService.findAllBySenderId((1L))).thenReturn(messages);
		
		mockMvc.perform(get("/messages/sent?userId=1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].content", is("testmessage1")))
        .andExpect(jsonPath("$[0].senderId", is(1))).andExpect(jsonPath("$[0].receiverId", is(2)));
		 assertEquals(2,messages.size());
	}
	
	@Test
	public void test_find_BySender_Id_return_Empty_List() throws Exception {

		List<User> users = getUsers();
		when(userService.getAll()).thenReturn(users);
		assertEquals(3, userService.getAll().size());

		
		when(messageService.findAllBySenderId((1L))).thenReturn(null);
		
		mockMvc.perform(get("/messages/sent?userId=1")).andExpect(status().isNoContent());
		assertNull(messageService.findAllBySenderId((1L)));
	}
	@Test
	public void test_find_MessageById() throws UserNotFoundException, InvalidMessageException {
		Message message = new Message(1L, "testmessage", 1L,2L);
		when(messageService.getById(1L)).thenReturn(Optional.of(message));

		Optional<Message> optionalMessage = messageService.getById(1L);
		if(optionalMessage.isPresent()) {
			message = optionalMessage.get();
			assertEquals("testmessage", message.getContent());
		}
	}
	
	
	
	@Test
    public void test_send_Message() throws Exception {
		User user = new User("sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com");
	    when(userService.exists(user)).thenReturn(false);
	    when(userService.save(user)).thenReturn(user);
	    
		Message message = new Message(1L, "testmessage", 1L,2L);
		when(messageService.save(message)).thenReturn(message);
		
		 mockMvc.perform(
	                post("/messages")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(asJsonString(message)))
	                .andExpect(status().isNotFound());
    }
	
	@Test
	public void deleteMessageById() throws Exception {
		Message message = new Message(1L, "testmessage", 1L,2L);
		when(messageService.getById(message.getId())).thenReturn(Optional.of(message));
	        doNothing().when(messageService).remove(message.getId());

	        mockMvc.perform(
	                delete("/messages/{id}", message.getId()))
	                .andExpect(status().isOk());

	        verify(messageService, times(1)).getById(message.getId());
	        verify(messageService, times(1)).remove(message.getId());
	        verifyNoMoreInteractions(userService);
	}
	
	@Test
	public void deleteMessageById_null() throws Exception {
		Message message = new Message(1L, "testmessage", 1L,2L);
		when(messageService.getById(message.getId())).thenReturn(Optional.of(message));
	        doNothing().when(messageService).remove(message.getId());

	        mockMvc.perform(
	                delete("/messages/{id}", 3))
	                .andExpect(status().isNotFound());
	        verifyNoMoreInteractions(userService);
	}
	
	private List<User> getUsers() throws DuplicateUserNameException {
		List<User> users = new ArrayList<>();
		users.add(new User(1L, "sandeep", "password", "sandeep", "vedavyas", "testuser1@gmail.com"));
		users.add(new User(2L, "sunayana", "password", "nanjegowda", "sunayana", "testuser1@gmail.com"));
		users.add(new User(3L, "kishor", "password", "kishor", "vedavyas", "testuser1@gmail.com"));
		return users;
	}
	
	private List<Message> getMessages() throws UserNotFoundException, InvalidMessageException {
		List<Message> messages = new ArrayList<>();
		messages.add(new Message(1L, "testmessage", 1L,2L));
		messages.add(new Message(2L, "testmessage2", 2L,3L));
		messages.add(new Message(3L, "testmessage3", 3L,3L));
		messages.add(new Message(4L, "testmessage5", 1L,3L));
		return messages;
	}
	
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
