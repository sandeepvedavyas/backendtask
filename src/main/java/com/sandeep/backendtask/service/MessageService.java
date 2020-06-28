package com.sandeep.backendtask.service;

import java.util.List;
import java.util.Optional;

import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.exception.UserNotFoundException;
import com.sandeep.backendtask.model.Message;


public interface MessageService {

	Optional<Message> getById(Long id);

	Message save(Message message) throws UserNotFoundException, InvalidMessageException;

	void remove(Long id);

	List<Message> getAllByReceiverIdAndSenderId(Long receiverId, Long senderId);
	List<Message> findAllBySenderId(Long senderId);
	List<Message> findAllByReceiverId(Long receiverId);

	List<Message> getAll();


	//void receiveMessage(MessageDto customMessage);

	//void receiveMessage(org.springframework.amqp.core.Message message);

}
