package com.sinans.springsecurityauthenticationauthorization.user;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepo extends JpaRepository<Users, String>, JpaSpecificationExecutor<Users> {
    Users findByUsername(String username);
    Page<Users> findAll(Pageable pageable);
}

