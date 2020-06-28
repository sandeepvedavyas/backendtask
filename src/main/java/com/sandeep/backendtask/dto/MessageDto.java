package com.sandeep.backendtask.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(description="All details about the Message")
public class MessageDto implements Serializable {

	private Long id;

	@NotNull
	@Size(min=4, message="Nickname Name should have atleast 2 characters")
	@ApiModelProperty(notes="Nickname Name should have atleast 2 characters")
	private String content;

	@Positive
	@NotNull
    @ApiModelProperty(notes="SenderId should be a positive number")
	private Long senderId;

	@Positive
	@NotNull
    @ApiModelProperty(notes="ReceiverId should be a positive number")
	private Long receiverId;

	public MessageDto() {

	}

	public MessageDto(Long id,
			@NotNull @Size(min = 4, message = "Nickname Name should have atleast 2 characters") String content,
			@Positive @NotNull Long senderId, @Positive @NotNull Long receiverId) {
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
		return "Message [id=" + id + ", content=" + content + ", senderId=" + senderId + ", receiverId=" + receiverId
				+ "]";
	}

}
