package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findAddressByName(String address);
}
