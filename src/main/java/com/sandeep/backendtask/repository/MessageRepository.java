package com.sandeep.backendtask.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sandeep.backendtask.model.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
	List<Message> findAllByReceiverIdAndSenderId(Long receiverId, Long senderId );
	List<Message> findAllBySenderId(Long senderId);
	List<Message> findAllByReceiverId(Long receiverId);
}

