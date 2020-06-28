package com.sandeep.backendtask.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandeep.backendtask.BackendtaskApplication;
import com.sandeep.backendtask.dto.MessageDto;
import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.exception.UserNotFoundException;
import com.sandeep.backendtask.model.Message;
import com.sandeep.backendtask.repository.MessageRepository;
import com.sandeep.backendtask.repository.UserRepository;
@Service
public class MessageServiceImpl implements MessageService {
	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;
	private final RabbitTemplate rabbitTemplate;

	public MessageServiceImpl(final RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public Optional<Message> getById(Long id) {
		return messageRepository.findById(id);
	}

	@Override
	public Message save(Message message) throws UserNotFoundException, InvalidMessageException {

		if (message.getSenderId().equals(message.getReceiverId())) {
			throw new InvalidMessageException("You cannot send message to yourself");
		}
		if (!userRepository.existsById(message.getSenderId())) {
			throw new UserNotFoundException(
					"You cannot send a message without senderId, this application is not having auth functionality yet, If not done please create a sender user");
		}

		if (!userRepository.existsById(message.getReceiverId())) {
			throw new UserNotFoundException("You cannot send a message without ReceiverId, Please enter receiver");
		}
		
		Message sentMessage = messageRepository.save(message);
		rabbitTemplate.convertAndSend(BackendtaskApplication.EXCHANGE_NAME, BackendtaskApplication.ROUTING_KEY,
				outbound(message));
		return sentMessage;
	}

	@Override
	public void remove(Long id) {
		messageRepository.deleteById(id);

	}

	@Override
	public List<Message> getAllByReceiverIdAndSenderId(Long receiverId, Long senderId) {
		return messageRepository.findAllByReceiverIdAndSenderId(receiverId, senderId);
	}

	@Override
	public List<Message> findAllBySenderId(Long senderId) {
		return messageRepository.findAllBySenderId(senderId);
	}

	@Override
	public List<Message> findAllByReceiverId(Long receiverId) {
		return messageRepository.findAllByReceiverId(receiverId);
	}

	@Override
	public List<Message> getAll() {
		return (List<Message>) messageRepository.findAll();
	}
		
	private MessageDto outbound(Message message) {
		MessageDto messageDto = new MessageDto();
		messageDto.setId(message.getId());
		messageDto.setContent(message.getContent());
		messageDto.setSenderId(message.getSenderId());
		messageDto.setReceiverId(message.getReceiverId());
		return messageDto;
	}
}
