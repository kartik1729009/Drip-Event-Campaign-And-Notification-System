package com.Kartik.notiflow.UserAuth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth>findByEmail(String email);
    @Query("""
        SELECT u
        FROM UserAuth u
        WHERE u.email = :email
        AND u.workspace.workspaceId = :workspaceId
        """)
    Optional<UserAuth> findWorkspaceUser(
        @Param("email") String email,
        @Param("workspaceId") Long workspaceId);
}
