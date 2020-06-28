package com.sandeep.backendtask.controller;

import java.text.ParseException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sandeep.backendtask.dto.MessageDto;
import com.sandeep.backendtask.exception.BaseException;
import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.helper.Response;
import com.sandeep.backendtask.model.Message;
import com.sandeep.backendtask.service.MessageService;
import com.sandeep.backendtask.service.UserService;


@RestController
public class MessageController {

	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private ModelMapper modelMapper;

	
	// =========================================== Get All Messages ==========================================
	@GetMapping(value = "/messages")
	public ResponseEntity<List<MessageDto>> getMessages() {
		logger.info("MessageController.clazz getMessages()");

		List<Message> messages = messageService.getAll();

		if (messages == null || messages.isEmpty()) {
			logger.info("no messages found");
			return new ResponseEntity<List<MessageDto>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<MessageDto>>(messages.stream().map(this::convertToDto).collect(Collectors.toList()), HttpStatus.OK);
	}

	// =========================================== Get all messages by sender and receiver =========================================
	
	@GetMapping(value = "/messages/received")
	public ResponseEntity<List<Message>> getMessageReceived(@RequestParam(name="userId") Long userId,@RequestParam(name="senderId",required = false) Optional<Long> senderId) throws InvalidMessageException {
		logger.info("MessageController.clazz getMessages received");
		
		List<Message> receivedMessages = new ArrayList<Message>();
		if(senderId.isPresent()) {
			receivedMessages = messageService.getAllByReceiverIdAndSenderId(userId,senderId.get());
		} else {
			receivedMessages = messageService.findAllByReceiverId(userId);
		}
		
		if (receivedMessages == null || receivedMessages.isEmpty()) {
			logger.info("no messages found");
			return new ResponseEntity<List<Message>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Message>>(receivedMessages, HttpStatus.OK);
	}
	
	
	// =========================================== Get all sent messages ========================================
	
	@GetMapping(value = "/messages/sent")
	public ResponseEntity<List<Message>> getMessageSent(@RequestParam(name="userId" ,required = true) Long userId) {
		logger.info("MessageController.clazz getMessages received");
		
		List<Message> receivedMessages = messageService.findAllBySenderId(userId);
		if (receivedMessages == null || receivedMessages.isEmpty()) {
			logger.info("no messages found");
			return new ResponseEntity<List<Message>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Message>>(receivedMessages, HttpStatus.OK);
		
	}
	
	// =========================================== Send message ========================================
	
	@PostMapping(value = "/messages")
	public ResponseEntity<Response> saveMessage(@Valid @RequestBody MessageDto messageDto,UriComponentsBuilder ucBuilder) throws BaseException, ParseException {
		logger.info("MessageController.clazz saveMessage() message" + messageDto);
		
		Message message = convertToEntity(messageDto);
		if (!userService.getById(message.getReceiverId()).isPresent()) {
			logger.info("No user exists with id :: {} ",message.getReceiverId());
			return new ResponseEntity<Response>(new Response(HttpStatus.NOT_FOUND.value(),"No user exists with id :: " + message.getReceiverId() + ", Please enter valid receiver id"),
					HttpStatus.NOT_FOUND);
		}
		
		if (!userService.getById(message.getSenderId()).isPresent()) {
			logger.info("No user exists with id :: {}", message.getSenderId());
			return new ResponseEntity<Response>(new Response(HttpStatus.NOT_FOUND.value(),"No user exists with id ::" + message.getSenderId() + ", Please enter valid sender id"),
					HttpStatus.NOT_FOUND);
		}
		
		
		Message createdMessage = messageService.save(message);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(createdMessage.getId()).toUri());
		return new ResponseEntity<Response>(headers, HttpStatus.CREATED);
	}
	
	// =========================================== Delete message by ID ========================================
	@DeleteMapping(value = "/messages/{id}")
	public ResponseEntity<Response> delete(@PathVariable("id") long id) throws InvalidMessageException {
		logger.info("MessageController.clazz delete()id:: {}",id);

		Optional<Message> optionalMessage = getMessagebyId(id);

		if (!optionalMessage.isPresent()) {
			logger.info("Message to delete can not be found::id:: {}",id);
			return new ResponseEntity<Response>(new Response(HttpStatus.NOT_FOUND.value(),"Unable to delete. Message not found with id" + id),
					HttpStatus.NOT_FOUND);
		}
		messageService.remove(id);
		
		return new ResponseEntity<Response>(
				new Response(HttpStatus.OK.value(), "Message has been deleted successfully"), HttpStatus.OK);
	}
		
	private Optional<Message> getMessagebyId(Long messageId) {
		Optional<Message> optionalMessage = messageService.getById(messageId);
		return optionalMessage;
	}
	
	private MessageDto convertToDto(Message message) {
		MessageDto messageDto = modelMapper.map(message, MessageDto.class);
	    return messageDto;
	}
	
	private Message convertToEntity(MessageDto messageDto) throws ParseException {
	    Message message = modelMapper.map(messageDto, Message.class);
	  
	    if (messageDto.getId() != null) {
	        Optional<Message> existingMessage = messageService.getById(messageDto.getId());
	        if(existingMessage.isPresent()) {
	        	message.setContent(existingMessage.get().getContent());
	        	message.setReceiverId(existingMessage.get().getReceiverId());
	        	message.setSenderId(existingMessage.get().getSenderId());
	        }
	    }
	    return message;
	}
}
