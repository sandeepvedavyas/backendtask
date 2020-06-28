package com.sandeep.backendtask.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the User")
public class UserDto {

	private Long id;

	@Size(min = 4, message = "Nickname Name should have atleast 2 characters")
	@ApiModelProperty(notes = "Nickname Name should have atleast 2 characters")
	private String nickName;

	@NotNull
	@Size(min = 2, message = "Password should have atleast 2 characters")
	@ApiModelProperty(notes = "Nickname Name should have atleast 2 characters")
	private String password;

	@NotNull
	@Size(min = 2, message = "First Name should have atleast 2 characters")
	@ApiModelProperty(notes = "First Name should have atleast 2 characters")
	private String firstName;

	@NotNull
	@Size(min = 2, message = "Last Name should have atleast 2 characters")
	@ApiModelProperty(notes = "Last Name should have atleast 2 characters")
	private String lastName;

	@NotNull
	@Email(message = "Should be a valid email")
	@ApiModelProperty(notes = "Should be a valid email")
	private String email;

	public UserDto() {
	}

	public UserDto(Long id) {
		this.id = id;
	}

	public UserDto(@Size(min = 4, message = "Nickname Name should have atleast 2 characters") String nickName,
			@NotNull @Size(min = 2, message = "Password should have atleast 2 characters") String password,
			@NotNull @Size(min = 2, message = "First Name should have atleast 2 characters") String firstName,
			@NotNull @Size(min = 2, message = "Last Name should have atleast 2 characters") String lastName,
			@NotNull @Email(message = "Should be a valid email") String email) {
		super();
		this.nickName = nickName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	

	public UserDto(Long id, @Size(min = 4, message = "Nickname Name should have atleast 2 characters") String nickName,
			@NotNull @Size(min = 2, message = "Password should have atleast 2 characters") String password,
			@NotNull @Size(min = 2, message = "First Name should have atleast 2 characters") String firstName,
			@NotNull @Size(min = 2, message = "Last Name should have atleast 2 characters") String lastName,
			@NotNull @Email(message = "Should be a valid email") String email) {
		super();
		this.id = id;
		this.nickName = nickName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", nickName=" + nickName + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

}
