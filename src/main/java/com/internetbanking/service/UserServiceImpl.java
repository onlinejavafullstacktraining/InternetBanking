package com.internetbanking.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.internetbanking.bean.AccountAutoBean;
import com.internetbanking.bean.EmailAutoBean;
import com.internetbanking.model.Account;
import com.internetbanking.model.AccountType;
import com.internetbanking.model.Contact;
import com.internetbanking.model.MonthlyAverageBalance;
import com.internetbanking.model.Role;
import com.internetbanking.model.User;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.UserRepository;
import com.internetbanking.resource.UserResource;
import com.internetbanking.util.BankUtilities;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
	Logger logger=LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private EmailSenderService emailService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PersonService personService;
	@Autowired
	private CacheService cacheService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);

		Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
		for (Role role : user.getRoles()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(String.valueOf(role.getName())));
		}
		/*
		 * else { throw new UsernameNotFoundException(MessageFormat.
		 * format("User with email {0} cannot be found.", username));
		 */
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				grantedAuthorities);

	}

	public String sendConfirmationMail(String userMail, String token) {
		return emailService.sendEmail(userMail,token);
		
	}

	@Override
	public void saveUser(User user) {
		final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		 userRepository.saveUser(user);
		

	}

	@Override
	public AccountAutoBean findByUsername(String username) {
		logger.info("findByUsername method is called with username: {}",username);
		AccountAutoBean accountBean=new AccountAutoBean();
		 User user = userRepository.findByUsername(username);
		 Account account=user.getPerson().getAccount();
		 MonthlyAverageBalance averageBalance=null;
		 if(user!=null && user.isEnabled() ) {
			 if(user.getPerson()!=null &&account==null) {
				  account =new Account(); 
				 int accountNumber=ThreadLocalRandom.current().nextInt(); 
				 account.setAccountNumber(String.valueOf(accountNumber));
				 account.setAccountPassword(user.getPassword());
				 account.setAccountBalance(1000.00);
				 account.setCreatedDate(new Date());
				 account.setAccountType(AccountType.savingAccount);
				 if(account.getMontlyAverageBalance()==null) {
					 averageBalance=new MonthlyAverageBalance();
					 averageBalance.setCity(user.getPerson().getAddress().getCity());
					 averageBalance.setMonthlyBalance(3000.00);
				 }
				// accountBean.setMessage("provided user is not available check with other username");
				 accountRepository.saveAccount(account);
				 averageBalance.setAccount(account);
				 accountRepository.saveMontlyAverageBalance(averageBalance);
				 cacheService.saveObject("accountInfo", account);
				 user.getPerson().setAccount(account);
				 logger.debug("person sort name: {}",user.getPerson().getSortname());
				 personService.savePerson(user.getPerson());
				 accountBean=convertObjectIntoBean(accountBean,account,averageBalance,user);
				
			 }else {
				 if(account!=null && account.getMontlyAverageBalance()==null){
					 averageBalance=new MonthlyAverageBalance();
					 averageBalance.setCity(user.getPerson().getAddress().getCity());
					 averageBalance.setMonthlyBalance(3000.00);
					 accountRepository.saveAccount(account);
					 averageBalance.setAccount(account);
					 accountRepository.saveMontlyAverageBalance(averageBalance);
				 }
			 }
		 }else 
			 accountBean.setMessage("Please check your email to activate the user");
		 return accountBean;
	}

	private AccountAutoBean convertObjectIntoBean(AccountAutoBean accountBean, Account account, MonthlyAverageBalance averageBalance, User user) {
		accountBean.setAccountBalance(account.getAccountBalance());
		accountBean.setAccountId(account.getAccountId());
		accountBean.setAccountNumber(account.getAccountNumber());
		accountBean.setAveragebalanceId(averageBalance.getAverageBalanceId());
		accountBean.setMonthlyBalance(averageBalance.getMonthlyBalance());
		accountBean.setFirstname(user.getPerson().getFirstname());
		accountBean.setGender(user.getPerson().getGender());
		accountBean.setLastName(user.getPerson().getLastname());
		accountBean.setPersonId(user.getPerson().getPersonId());
		accountBean.setSortname(user.getPerson().getSortname());
		return accountBean;
	}

	@Override
	public ResponseEntity<String> updateLoginPassword(String username,String oldPassword, String newPassword) {
		 User user = userRepository.findByUsername(username);
		 final boolean encryptedPassword = bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
		 String statusMessage=null;
		 if(user!=null && encryptedPassword) {
			 userRepository.saveUser(user);
			 statusMessage="New Password is updated successfully";
			 return new ResponseEntity<String>(statusMessage,HttpStatus.OK);
		 }else
			 statusMessage="Password is not matched";
		return new ResponseEntity<String>(statusMessage,HttpStatus.OK);
	}

	@Override
	public String forgetPassword(String email) {
		List<Contact> findContactbyEmail = userRepository.findContactbyEmail(email);
		if(!BankUtilities.isEmptyList(findContactbyEmail)) {
			return emailService.sendForgetPasswordEmail(email);
		}
		return null;
	}

	@Override
	public String forgetPasswordConfirmation(EmailAutoBean emailAutoBean) {
		List<Contact> findContactbyEmail = userRepository.findContactbyEmail(emailAutoBean.getEmail());
		if(!BankUtilities.isEmptyList(findContactbyEmail)) {
			findContactbyEmail.stream().forEach(contact->{
				//contact.getPerson().getUser().setPassword(emailAutoBean.getPassword());
				//updateLoginPassword(contact.getPerson().getUser().getUsername(), contact.getPerson().getUser().getPassword(), emailAutoBean.getPassword());
				final String encryptedPassword = bCryptPasswordEncoder.encode(emailAutoBean.getPassword());
				contact.getPerson().getUser().setPassword(encryptedPassword);
				 userRepository.saveUser(contact.getPerson().getUser());
				});
		}
		return "New Password is updated successfully";
	}
	
	

}
