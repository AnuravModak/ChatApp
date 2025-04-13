package com.demo.chatApp.keyManager;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface KeyRepository extends JpaRepository<KeyEntity,Long> {


    @Query(value = "SELECT id FROM master_keys WHERE active = true LIMIT 1", nativeQuery = true)
    public Long findActiveKeyId();

    @Transactional
    @Modifying
    @Query(value = "UPDATE master_keys SET expires_at = CURRENT_TIMESTAMP, active=false WHERE id = :id", nativeQuery = true)
    void expireKeyById(@Param("id") Long id);

    @Query(value="SELECT master_keys.private_key from master_keys WHERE active=true and id= :id",nativeQuery = true)
    public String findActivePrivateKey(@Param("id") Long id);

    @Query(value="SELECT master_keys.public_key from master_keys WHERE active=true and id= :id",nativeQuery = true)
    public String findActivePublicKey(@Param("id") Long id);


}
