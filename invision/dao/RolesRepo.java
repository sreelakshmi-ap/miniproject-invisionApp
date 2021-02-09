package com.miniproj.invision.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miniproj.invision.model.*;

public interface RolesRepo extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(ERoles name);

}
