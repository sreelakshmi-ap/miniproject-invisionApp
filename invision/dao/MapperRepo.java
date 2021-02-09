package com.miniproj.invision.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.miniproj.invision.model.User_Qnr_Mapper;

public interface MapperRepo extends JpaRepository<User_Qnr_Mapper, Integer> {
	
	@Query(value = "SELECT * FROM invision.user_qnr_mapper where user_qnr_mapper.emp_num =?1 and user_qnr_mapper.q_id=?2",nativeQuery = true)
	Optional<User_Qnr_Mapper> findByEmp_numAndQ_id(String emp_id,int q_id);
	
	@Query(value = "SELECT user_qnr_mapper.q_id FROM user_qnr_mapper where user_qnr_mapper.status = 0 and user_qnr_mapper.emp_num =?",nativeQuery = true)
	List<Integer> getPendingQuestionnaires(String emp_num);
	
	@Query(value = "SELECT user_qnr_mapper.q_id FROM user_qnr_mapper where user_qnr_mapper.status = 1 and user_qnr_mapper.emp_num =?",nativeQuery = true)
	List<Integer> getCompletedQuestionnaires(String emp_num);
	
	@Query(value = "select q.title, e.username, m.status, m.date_accepted, m.date_mail_sent from "
			+ "invision.employees e, invision.questionnaire q, invision.user_qnr_mapper m\r\n"
			+ "where e.emp_num in(select emp_num from invision.user_qnr_mapper where q_id =?1)\r\n"
			+ "and q.q_id =?1\r\n"
			+ "and m.q_id =?1\r\n"
			+ "and (q.q_id = m.q_id)\r\n"
			+ "and (e.emp_num = m.emp_num)", nativeQuery = true)
	List<String> getReport(Integer q_id);
	

}
