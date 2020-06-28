package com.sandeep.backendtask.service;

import java.util.List;
import java.util.Optional;

import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.model.User;

public interface UserService {
	Optional<User> getById(Long id);

	User save(User user) throws DuplicateUserNameException;

	void remove(Long id);
	
	User getByNickName(String username);

	List<User> getAll();
	
	boolean exists(User user);
}
