package com.portfoliopro.repository;

import com.portfoliopro.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByExecutedAtDesc(Long userId);
    List<Transaction> findByUserIdAndTickerOrderByExecutedAtDesc(Long userId, String ticker);
    List<Transaction> findTop20ByUserIdOrderByExecutedAtDesc(Long userId);
}
