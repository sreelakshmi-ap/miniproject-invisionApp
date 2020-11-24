package com.miniproj.invision.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miniproj.invision.model.Status;



public interface StatusRepo extends JpaRepository<Status, Integer> {

}
