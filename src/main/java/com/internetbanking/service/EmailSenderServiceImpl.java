package com.internetbanking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService{
	@Autowired
	private JavaMailSender javaMailSender;

	private static final String EMAIL_ID="javatechnology.github@gmail.com"; 

	@Override
	public String sendEmail(String userMail, String token) {
		
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(userMail);
		mailMessage.setSubject("Account Activation Link!");
		mailMessage.setFrom(EMAIL_ID);
		String message="Thank you for registering. Please click on the below link to activate your account.";
		mailMessage.setText(message+"\r\n"+ "http://localhost:3000/sign-up/confirm?"+token);
		 javaMailSender.send(mailMessage);
	
		return "Check your Email for activation";
		
		
	}

	@Override
	public String sendForgetPasswordEmail(String email) {
		
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject("Reset your password");
		mailMessage.setFrom(EMAIL_ID);
		String message="Hi,\r\n"
				+ "\r\n"
				+ "We have received a request to reset your password.\r\n"
				+ "\r\n"
				+ "Click on the button below within the next 90 minutes to reset your password. If you ignore this message, your password won’t be changed.";
		mailMessage.setText(message+"\r\n"+ "http://localhost:3000/forgetPassword/confirmation");
		 javaMailSender.send(mailMessage);
	
		return "Check your Email Reset your password";
	}

}
