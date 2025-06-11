package com.prata.dscatalog.repositories;


import com.prata.dscatalog.entities.Product;
import com.prata.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
