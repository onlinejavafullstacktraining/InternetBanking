package com.internetbanking.service;

public interface EmailSenderService {
	public String sendEmail(String userMail, String token);
	public String sendForgetPasswordEmail(String email);

}
