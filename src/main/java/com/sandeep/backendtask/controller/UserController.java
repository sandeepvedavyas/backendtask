package com.sandeep.backendtask.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sandeep.backendtask.dto.UserDto;
import com.sandeep.backendtask.exception.BaseException;
import com.sandeep.backendtask.exception.UserException;
import com.sandeep.backendtask.helper.Response;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.service.UserService;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	// =========================================== Get All Users // ==========================================
	@GetMapping(value = "/users")
	public ResponseEntity<List<UserDto>> getUsers() {
		logger.debug("UserController.clazz getUsers()");

		List<User> users = userService.getAll();

		if (users == null || users.isEmpty()) {
			logger.debug("no users found");
			return new ResponseEntity<List<UserDto>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserDto>>(users.stream().map(this::convertToDto).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	// =========================================== Get User By ID // =========================================
	@GetMapping(value = "/users/{id}")
	public ResponseEntity<User> getUser(@PathVariable("id") long id) throws UserException {
		logger.debug("UserController.clazz getUser() id :: {} ",id);

		Optional<User> user = getUserById(id);
		if (!user.isPresent()) {
			logger.info("UserController.clazz getUser() can not be found with id:: {}", id);
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(user.get());
	}

	// =========================================== Create New User // ========================================
	@PostMapping(value = "/users")
	public ResponseEntity<Response> saveUser(@Valid @RequestBody UserDto userDto, UriComponentsBuilder ucBuilder)
			throws BaseException, ParseException {

		User user = convertToEntity(userDto);
		if (userService.exists(user)) {
			logger.info("a user with name {} already exists",user.getNickName());
			return new ResponseEntity<Response>(new Response(HttpStatus.CONFLICT.value(),
					"NickName is already taken!,Please use different nickname"), HttpStatus.CONFLICT);
		}
		logger.debug("UserController.clazz saveUser() user {} " , user);
		userService.save(user);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<Response>(headers, HttpStatus.CREATED);
	}

	// =========================================== Update Existing User  ===================================//
	@PutMapping(value = "/users/{id}")
	public ResponseEntity<UserDto> updateUser(@PathVariable long id, @Valid @RequestBody UserDto userDto) throws BaseException, ParseException {
		logger.info("UserController.clazz updateUser() User {}", userDto);

		Optional<User> currentUser = userService.getById(id);

		if (!currentUser.isPresent()) {
			logger.info("User with id {} not found", id);
			return new ResponseEntity<UserDto>(HttpStatus.NOT_FOUND);
		}
		
		logger.info("updating user: {}", userDto);
		userDto.setId(id);
		User user = convertToEntity(userDto);

		return new ResponseEntity<UserDto>(convertToDto(userService.save(user)), HttpStatus.OK);
	}

	// =========================================== Delete User // ===================================
	@DeleteMapping(value = "/users/{id}")
	public ResponseEntity<Response> delete(@PathVariable("id") long id) throws UserException {
		logger.debug("UserController.clazz delete() id :: {}",id);

		Optional<User> optionalUser = getUserById(id);

		if (!optionalUser.isPresent()) {
			logger.info("User to delete can not be found::id:: {}",id);
			return new ResponseEntity<Response>(
					new Response(HttpStatus.NOT_FOUND.value(), "Unable to delete. User not found with id::" + id),
					HttpStatus.NOT_FOUND);
		}

		userService.remove(id);

		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "User has been deleted successfully"),
				HttpStatus.OK);
	}

	private Optional<User> getUserById(long id) {
		Optional<User> user = userService.getById(id);
		return user;
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