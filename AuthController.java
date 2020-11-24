package com.miniproj.invision.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;

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

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.payload.request.LoginRequest;
import com.miniproj.invision.payload.request.SignUpRequest;
import com.miniproj.invision.payload.response.JwtResponse;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.security.JwtUtils;
import com.miniproj.invision.security.MyUserDetails;

@RestController
@RequestMapping("/authenticate")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	EmployeeRepo userRepository;

	@Autowired
	RolesRepo roleRepository;
	
	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	
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
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
   
    public ResponseEntity<?> currentUserNameSimple(@RequestBody Employees emp) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();
	  
	    Employees employee = userRepository.findByUsername(currentUserName).get();
	    String encodedPwd = encoder.encode(emp.getPassword());
	    employee.setPassword(encodedPwd);
	    
	    userRepository.save(employee);

	    return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));

	    }
		
}
