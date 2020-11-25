package com.miniproj.invision.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.miniproj.invision.model.Employees;

public interface EmployeeRepo extends JpaRepository<Employees, String> {

	
	@Query(value = "SELECT * FROM invision.employees where emp_num in(select emp_num from invision.users_roles where role_id = 1)",nativeQuery = true)
	List<Employees> findAllSuperAdmins();
	
	@Query(value = "SELECT * FROM invision.employees where emp_num in(select emp_num from invision.users_roles where role_id = 2)",nativeQuery = true)
	List<Employees> findAllAdmins();
	
	@Query("SELECT e.email from  Employees e WHERE e.username =:username")
	String getEmailByUsername(@Param("username") String username);
	
	Optional<Employees> findByUsername(String username);
	

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

}
