package com.portfoliopro.repository;

import com.portfoliopro.model.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findByUserId(Long userId);
    Optional<PortfolioItem> findByUserIdAndTicker(Long userId, String ticker);
    void deleteByUserIdAndTicker(Long userId, String ticker);
}
