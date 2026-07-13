package com.Kartik.notiflow.UserAuth;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ConflictException;
import com.Kartik.notiflow.Common.Exception.ResourceNotFoundException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Config.JwtService;
import com.Kartik.notiflow.Enum.Role;
import com.Kartik.notiflow.Security.CurrentAuth;
import com.Kartik.notiflow.UserAuth.Dto.LoginResponse;
import com.Kartik.notiflow.UserAuth.Dto.LoginUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserResponse;
import com.Kartik.notiflow.UserAuth.Dto.UpdateRoleRequest;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuthRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final WorkspaceAuthRepository workspaceAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final CurrentAuth currentAuth;

    // helper: map entity -> response dto
    private RegisterUserResponse toResponseDto(UserAuth user) {
        RegisterUserResponse dto = modelMapper.map(user, RegisterUserResponse.class);
        dto.setWorkspaceName(user.getWorkspace().getWorkspaceName());
        return dto;
    }

    // register first admin — workspace auth via JWT, fails if workspace already has users
    public ResponseEntity<ApiResponseHandler<Object>> registerAdmin(RegisterUserRequest request) {
        try {
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            // block if workspace already has users — admin already exists
            if (userAuthRepository.existsByWorkspace(workspace)) {
                throw new BadRequestException(
                        "Workspace already has users. Admin already exists. Use /register to add more users.");
            }

            boolean emailExists = userAuthRepository.findByEmail(request.getEmail()).isPresent();
            if (emailExists) {
                throw new ConflictException("User already exists with email: " + request.getEmail());
            }

            UserAuth admin = new UserAuth();
            admin.setUserName(request.getUserName());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRole(Role.ADMIN); // first user is always ADMIN regardless of request body
            admin.setActive(true);
            admin.setWorkspace(workspace);

            UserAuth saved = userAuthRepository.save(admin);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "Admin created successfully.",
                    toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BadRequestException | ConflictException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // register a new user — user auth via JWT, only ADMIN can call this
    public ResponseEntity<ApiResponseHandler<Object>> registerUser(RegisterUserRequest request) {
        try {
            UserAuth admin = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!admin.getRole().equals(Role.ADMIN)) {
                throw new BadRequestException("Only an Admin can create users.");
            }

            boolean emailExists = userAuthRepository.findByEmail(request.getEmail()).isPresent();
            if (emailExists) {
                throw new ConflictException("User already exists with email: " + request.getEmail());
            }

            if (request.getRole() == null) {
                throw new BadRequestException("Role is required to create a user.");
            }

            UserAuth user = new UserAuth();
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setActive(true);
            user.setWorkspace(workspace);

            UserAuth saved = userAuthRepository.save(user);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "User created successfully.",
                    toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BadRequestException | ConflictException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Database error: invalid data constraint.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // login — workspace resolved from body, checks user active status
    public ResponseEntity<ApiResponseHandler<Object>> loginUser(LoginUserRequest request) {
        try {
            WorkspaceAuth workspace = workspaceAuthRepository
                    .findByWorkspaceName(request.getWorkspaceName())
                    .orElseThrow(() -> new BadRequestException(
                            "Workspace does not exist with name: " + request.getWorkspaceName()));

            UserAuth user = userAuthRepository
                    .findWorkspaceUser(request.getEmail(), workspace.getWorkspaceId())
                    .orElseThrow(() -> new BadRequestException(
                            "User does not exist with email: " + request.getEmail()));

            // block login if user is inactive
            if (!Boolean.TRUE.equals(user.getActive())) {
                throw new BadRequestException("User account is deactivated. Contact your admin.");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadRequestException("Invalid credentials.");
            }

            String accessToken = jwtService.generateUserAccessToken(user);
            String refreshToken = jwtService.generateUserRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "Login successful.", loginResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // update role of a user — ADMIN only
    public ResponseEntity<ApiResponseHandler<Object>> updateRole(Long userId, UpdateRoleRequest request) {
        try {
            UserAuth admin = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!admin.getRole().equals(Role.ADMIN)) {
                throw new BadRequestException("Only an Admin can update user roles.");
            }

            UserAuth user = userAuthRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // ensure user belongs to same workspace
            if (!user.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("User does not belong to your workspace.");
            }

            user.setRole(request.getRole());
            UserAuth saved = userAuthRepository.save(user);

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "User role updated successfully.", toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // toggle active status of a user — ADMIN only
    public ResponseEntity<ApiResponseHandler<Object>> toggleStatus(Long userId) {
        try {
            UserAuth admin = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!admin.getRole().equals(Role.ADMIN)) {
                throw new BadRequestException("Only an Admin can change user status.");
            }

            UserAuth user = userAuthRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            if (!user.getWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId())) {
                throw new BadRequestException("User does not belong to your workspace.");
            }

            // flip the active status
            user.setActive(!Boolean.TRUE.equals(user.getActive()));
            UserAuth saved = userAuthRepository.save(user);

            String message = Boolean.TRUE.equals(saved.getActive())
                    ? "User activated successfully."
                    : "User deactivated successfully.";

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, message, toResponseDto(saved));
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // get all users in the workspace — ADMIN only
    public ResponseEntity<ApiResponseHandler<Object>> getAllUsers() {
        try {
            UserAuth admin = currentAuth.getUser();
            WorkspaceAuth workspace = currentAuth.getWorkspace();

            if (!admin.getRole().equals(Role.ADMIN)) {
                throw new BadRequestException("Only an Admin can view all users.");
            }

            List<UserAuth> users = userAuthRepository.findByWorkspace(workspace);
            List<RegisterUserResponse> dtos = users.stream().map(this::toResponseDto).toList();

            ApiResponseHandler<Object> response = ResponseBuilder.success(
                    HttpStatus.OK, "All users retrieved.", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("An unexpected error occurred: " + ex.getMessage());
        }
    }
}
