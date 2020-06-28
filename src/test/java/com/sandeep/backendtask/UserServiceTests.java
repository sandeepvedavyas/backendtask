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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.repository.UserRepository;
import com.sandeep.backendtask.service.UserServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = { "/schema.sql","/data.sql" }, config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
public class UserServiceTests {

	@Mock
	UserRepository userRepository;

	@Autowired
	private UserServiceImpl userService;

	@Before
	public void init() {
	}

	@Test
	public void test_when_save_return_user() throws DuplicateUserNameException {
		User user = new User(10000L, "sandeep-test", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		when(userRepository.save(user)).thenReturn(user);

		User created = userService.save(user);
		assertSame("should be same", created.getLastName(), user.getLastName());
	}

	@Test
	public void test_when_getAll_return_user() throws DuplicateUserNameException {
		User user = new User(1000L, "sandeep-test", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		when(userRepository.save(user)).thenReturn(user);

		userService.save(user);
		assertEquals(4, ((List<User>) userService.getAll()).size());
	}

	@Test
	public void test_when_getAll_return_user_by_id() throws DuplicateUserNameException {
		assertNotNull((userService.getById(1000L).get().getNickName()));
	}

	@Test
	public void test_when_delete_return_user_by_id() throws DuplicateUserNameException {
		User user = new User(1000L, "sandeep-test", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		when(userRepository.save(user)).thenReturn(user);

		userService.save(user);

		userService.remove(user.getId());
		assert (!userService.getById(1000L).isPresent());
	}
	
	@Test
	public void test_when_get_by_nickName_return_user() throws DuplicateUserNameException {
		User user = new User(1L, "sandeep-test", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		when(userRepository.save(user)).thenReturn(user);

		userService.save(user);
		assertEquals ("sandeep-test",userService.getByNickName(user.getNickName()).getNickName());
	}
	
	@Test
	public void test_user_exists_return_user() throws DuplicateUserNameException {
		User user = new User(1L, "sandeepssssssss", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		when(userRepository.save(user)).thenReturn(user);

		userService.save(user);
		assertEquals (true,userService.exists(user));
	}
	
	@Test
	public void test_user_exists_return_user_false() throws DuplicateUserNameException {
		User user = new User(10L, "abc", "password", "sandeep", "vedavyas", "testuser1@gmail.com");

		assertEquals (false,userService.exists(user));
	}
}
