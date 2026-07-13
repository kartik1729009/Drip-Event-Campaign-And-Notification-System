package com.Kartik.notiflow.UserAuth;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByEmail(String email);

    // find user by email scoped to a specific workspace
    @Query("""
            SELECT u FROM UserAuth u
            WHERE u.email = :email
            AND u.workspace.workspaceId = :workspaceId
            """)
    Optional<UserAuth> findWorkspaceUser(
            @Param("email") String email,
            @Param("workspaceId") Long workspaceId);

    // check if any user exists for a workspace — used to guard registerAdmin
    boolean existsByWorkspace(WorkspaceAuth workspace);

    // get all users for a workspace — used by admin
    List<UserAuth> findByWorkspace(WorkspaceAuth workspace);
}
