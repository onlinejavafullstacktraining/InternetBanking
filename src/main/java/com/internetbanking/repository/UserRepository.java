package com.internetbanking.repository;

import java.util.List;

import com.internetbanking.model.Contact;
import com.internetbanking.model.User;


public interface UserRepository {
	//List<User> findByUsername(String username);
	User findByUsername(String username);
	public void saveUser(User user);
	public User updateLoginPassword(String username,String oldPassword, String newPassword,String retryPassword);
	public List<Contact> findContactbyEmail(String email);

}
