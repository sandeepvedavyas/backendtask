package com.sandeep.backendtask.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.model.User;
import com.sandeep.backendtask.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public Optional<User> getById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User save(User user) throws DuplicateUserNameException {
		User temp = userRepository.findFirstByNickName(user.getNickName());
		if (temp != null && !(temp.getId().equals(user.getId()))) {
			throw new DuplicateUserNameException("NickName is already taken!,Please use different nickname");
		}
		return userRepository.save(user);
	}

	@Override
	public void remove(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public User getByNickName(String nickName) {
		return userRepository.findFirstByNickName(nickName);
	}

	@Override
	public List<User> getAll() {
		return (List<User>) userRepository.findAll();
	}

	@Override
	public boolean exists(User user) {
		return userRepository.findFirstByNickName(user.getNickName()) != null;
	}

}
