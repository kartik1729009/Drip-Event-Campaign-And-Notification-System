package com.Kartik.notiflow.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.Kartik.notiflow.UserAuth.UserAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;

@Component
public class CurrentAuth {
    public UserAuth getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserAuth user)) {
            throw new RuntimeException("User not authenticated");
        }
        return user;
    }

    public WorkspaceAuth getWorkspace() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof WorkspaceAuth workspace)) {
            throw new RuntimeException("workspace not authenticated");
        }
        return workspace;
    }
}
