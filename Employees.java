package com.miniproj.invision.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
public class Employees {

	@Id
	@Column(name = "emp_num")
	String emp_num;
	
	String username;
	String password;
	String email;
	String image_path;
	
	   @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	    @JoinTable(
	            name = "users_roles",
	            joinColumns = @JoinColumn(name = "emp_num"),
	            inverseJoinColumns = @JoinColumn(name = "role_id")
	            )
	    private Set<Role> role = new HashSet<>();
   
		public Employees(String emp_num, String username, String password, String email, String image_path) {
			this.emp_num = emp_num;
			this.username = username;
			this.password = password;
			this.email = email;
			this.image_path = image_path;
		}
		
		public Employees(String emp_num, String username, String email) {
			this.emp_num = emp_num;
			this.username = username;
			this.email = email;
		}

		public void setEmp_num(String emp_num) {
			this.emp_num = emp_num;
		}

	  
	public String getEmp_num() {
		return emp_num;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	


	public Set<Role> getRoles() {
		return role;
	}


	public void setRoles(Set<Role> role) {
		this.role = role;
	}

	public Employees() {}
	
	public String generatePassword()
	{
		 String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	      String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
	      String specialCharacters = "!@#$";
	      String numbers = "1234567890";
	      String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
	      Random random = new Random();
	      char[] password = new char[8];

	      password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
	      password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
	      password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
	      password[3] = numbers.charAt(random.nextInt(numbers.length()));
	   
	      for(int i = 4; i< 8 ; i++) {
	         password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
	      }
	      return password.toString();
	}
	  
}
