package com.miniproj.invision.security;
import java.util.ArrayList;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.model.Employees;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	 @Autowired
	 private EmployeeRepo userDao;

	 @Override
		@Transactional
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			Employees user = userDao.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

			return MyUserDetails.build(user);
		}

	}
	