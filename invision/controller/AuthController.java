package com.miniproj.invision.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.miniproj.invision.payload.request.LoginRequest;
import com.miniproj.invision.security.JwtUtils;
import com.miniproj.invision.security.MyUserDetails;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.dao.*;
import com.miniproj.invision.payload.response.*;
import com.miniproj.invision.model.*;
import com.miniproj.invision.payload.request.*;

@RestController
@RequestMapping("/authenticate")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	EmployeeRepo userRepository;
	
	@Autowired
	RolesRepo roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	MailService mailService;
	
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
													loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getEmp_num(), 
												 userDetails.getUsername(),
												 userDetails.getEmail(),
												 roles,
												 userDetails.getImage_path()
												 ));
	}
/*	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: User with same username already exist.! Try another username"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		Employees user = new Employees(signUpRequest.getEmp_num(), signUpRequest.getUsername(),
				 			encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getEmail(),
							 signUpRequest.getImage_path());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> role = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERoles.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			role.add(userRole);
		} else {
			strRoles.forEach(roles -> {
				switch (roles) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERoles.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					role.add(adminRole);

					break;
				case "super":
					Role modRole = roleRepository.findByName(ERoles.ROLE_SUPERADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					role.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERoles.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					role.add(userRole);
				}
			});
		}

		user.setRoles(role);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	*/
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.PUT)
	public ResponseEntity<?> sendMailWithNewPassword (@RequestBody Employees emp) throws MailException, MessagingException
	{
	    Employees employee = userRepository.findByUsername(emp.getUsername()).get();

	    String newPassword = employee.generatePassword();
	    String encodedPassword = encoder.encode(newPassword);
	    
	    employee.setPassword(encodedPassword);
	    
	    String toUser = employee.getEmail();
	    String subject = "Password Reset";
	    String body = "Your login password has been updated to "+newPassword+" "
	    		+ " NOTE: You should use THIS password for all the logins from now";
	    
	    mailService.sendEmail(toUser, subject, body);
	    
	    userRepository.save(employee);
	    
	    return ResponseEntity.ok(new MessageResponse("Check your mail "+toUser+" for updated password"));   
	}
		
}
