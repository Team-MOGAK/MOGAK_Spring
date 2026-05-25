package com.mogak.spring.repository;

import com.mogak.spring.domain.user.ConsentItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentItemRepository extends JpaRepository<ConsentItem, Long> {
    List<ConsentItem> findAllByActiveTrueOrderByDisplayOrderAscIdAsc();
}
