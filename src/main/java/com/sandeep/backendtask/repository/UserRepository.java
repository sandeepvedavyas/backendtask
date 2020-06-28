package com.sandeep.backendtask.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sandeep.backendtask.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	User findFirstByNickName(String username);
}