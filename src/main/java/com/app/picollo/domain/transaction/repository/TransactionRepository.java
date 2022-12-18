package com.app.picollo.domain.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.picollo.domain.transaction.dto.TopUserResponse;
import com.app.picollo.domain.transaction.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByUsername(String username);

    @Query("SELECT new com.app.picollo.domain.transaction.dto.TopUserResponse(t.username as username, sum(t.amount) as transactedValue) FROM Transaction t WHERE t.type = ?1 GROUP BY t.username ORDER BY sum(t.amount) desc LIMIT 10")
    List<TopUserResponse> findByType(String type);

    List<Transaction> findByTypeAndUsernameOrToUsername(String type, String username, String toUsername, Pageable page);
}
