package com.example.blog.repository;

import com.example.blog.domain.CouponPolicy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select cp from CouponPolicy cp where cp.id = :id")
//    Optional<CouponPolicy> findByIdWithLock(Long id);

    @Modifying
    @Query("UPDATE CouponPolicy cp SET cp.issuedCount = cp.issuedCount + 1 WHERE cp.id = :id AND cp.issuedCount < cp.totalCount")
    int increaseIssuedCoundIfAvailable(Long id);
}
