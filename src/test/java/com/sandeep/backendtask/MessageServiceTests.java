package com.sandeep.backendtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.exception.UserNotFoundException;
import com.sandeep.backendtask.model.Message;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.repository.MessageRepository;
import com.sandeep.backendtask.service.MessageServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql","/data.sql" }, config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public class MessageServiceTests {

	@Mock
	MessageRepository messageRepository;

	@Autowired
	private MessageServiceImpl messageService;

	@Before
	public void init() {
	}

	@Test
	public void test_when_save_return_user() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,2000L);
		//when(messageService.save(message)).thenReturn(message);

		Message created = messageService.save(message);
		assertSame("should be same", created.getContent(), message.getContent());
	}
	
	@Test(expected = UserNotFoundException.class)
	public void test_when_save_return_user_receiver_null() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,10L);
		//when(messageService.save(message)).thenReturn(message);

		Message created = messageService.save(message);
		assertSame("should be same", created.getContent(), message.getContent());
	}
	
	@Test(expected = UserNotFoundException.class)
	public void test_when_save_return_user_sender_null() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 10L,2l);
		//when(messageService.save(message)).thenReturn(message);

		Message created = messageService.save(message);
		assertSame("should be same", created.getContent(), message.getContent());
	}
	
	@Test(expected = InvalidMessageException.class)
	public void test_when_save_return_sender_receiver_same() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,1000L);
		//when(messageService.save(message)).thenReturn(message);

		Message created = messageService.save(message);
		assertSame("should be same", created.getContent(), message.getContent());
	}


	@Test
	public void test_when_getAll_return_message_by_id() throws DuplicateUserNameException {
		assertNotNull((messageService.getById(11000L).get().getContent()));
	}

	@Test
	public void test_when_delete_return_user_by_id() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(11000L, "testmessage", 1000L,2000L);
		when(messageRepository.save(message)).thenReturn(message);

		messageService.save(message);

		messageService.remove(message.getId());
		assert (!messageService.getById(1000L).isPresent());
	}
	
	@Test
	public void test_when_get_by_nickName_return_user() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,2000L);
		when(messageRepository.save(message)).thenReturn(message);

		messageService.save(message);
		assertNotNull(messageService.getAllByReceiverIdAndSenderId(message.getReceiverId(),message.getSenderId()));
	}
	
	@Test
	public void test_when_getAll_return_user() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,2000L);
		when(messageRepository.save(message)).thenReturn(message);

		messageService.save(message);
		assertEquals(9, ((List<Message>) messageService.getAll()).size());
	}
	
	@Test
	public void test_when_getAll_byReceiverId() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,2000L);
		when(messageRepository.save(message)).thenReturn(message);
		messageService.save(message);
		assertEquals(2, ((List<Message>) messageService.findAllByReceiverId(1000L)).size());
	}
	
	@Test
	public void test_when_getAll_bySenderId() throws DuplicateUserNameException, UserNotFoundException, InvalidMessageException {
		Message message = new Message(1000L, "testmessage", 1000L,2000L);
		when(messageRepository.save(message)).thenReturn(message);
		messageService.save(message);
		assertNotNull(messageService.findAllBySenderId((1000L)).size());
	}
}
