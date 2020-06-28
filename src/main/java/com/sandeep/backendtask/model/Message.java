package com.sandeep.backendtask.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
		
	private String content;
	
	private Long senderId;
	
	private Long receiverId;
	

	public Message() {
		
	}
	
	public Message(Long id,String content,Long senderId,Long receiverId) {
		super();
		this.id = id;
		this.content = content;
		this.senderId = senderId;
		this.receiverId = receiverId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", content=" + content + ", senderId=" + senderId + ", receiverId=" + receiverId + "]";
	}
	

}
