package com.mogak.spring.repository;

import com.mogak.spring.domain.consent.ConsentItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentItemRepository extends JpaRepository<ConsentItem, Long> {
    List<ConsentItem> findAllByActiveTrueOrderByIdAsc();
}
