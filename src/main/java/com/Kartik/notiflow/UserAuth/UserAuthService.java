package com.Kartik.notiflow.UserAuth;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Common.Exception.BadRequestException;
import com.Kartik.notiflow.Common.Exception.ConflictException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Config.JwtService;
import com.Kartik.notiflow.UserAuth.Dto.LoginUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserRequest;
import com.Kartik.notiflow.UserAuth.Dto.RegisterUserResponse;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuth;
import com.Kartik.notiflow.WorkspaceAuth.WorkspaceAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiry}")
    private long ACCESS_TOKEN_EXPIRY;

    @Value("${jwt.refresh-token-expiry}")
    private long REFRESH_TOKEN_EXPIRY;

    private final UserAuthRepository userAuthRepository;
    private final WorkspaceAuthRepository workspaceAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public ResponseEntity<ApiResponseHandler<Object>> registerUser(
            RegisterUserRequest registerRequest) {

        try {

            WorkspaceAuth workspaceAuth = workspaceAuthRepository
                    .findById(registerRequest.getWorkspaceId())
                    .orElseThrow(() -> new BadRequestException(
                            "Workspace does not exist with id: "
                                    + registerRequest.getWorkspaceId()));

            Boolean userExists = userAuthRepository
                    .findByEmail(registerRequest.getEmail())
                    .isPresent();

            if (userExists) {

                throw new ConflictException(
                        "User already exists with email: "
                                + registerRequest.getEmail());
            }

            UserAuth userAuth = modelMapper.map(
                    registerRequest,
                    UserAuth.class);

            userAuth.setPassword(
                    passwordEncoder.encode(
                            registerRequest.getPassword()));

            userAuth.setWorkspace(workspaceAuth);

            UserAuth savedUser = userAuthRepository.save(userAuth);

            RegisterUserResponse registerResponse = new RegisterUserResponse(
                    savedUser.getUserAuthId(),
                    savedUser.getUserName(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    savedUser.getActive(),
                    workspaceAuth.getWorkspaceId(),
                    savedUser.getCreatedAt());

            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.CREATED,
                    "User created successfully",
                    registerResponse);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(successResponse);

        } catch (ConflictException ex) {

            throw new BadRequestException(
                    "Database error: "
                            + ex.getMessage());

        } catch (DataIntegrityViolationException ex) {

            throw new BadRequestException(
                    "Database error: Could not save user due to invalid data constraint.");

        } catch (BadRequestException ex) {

            throw ex;

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: "
                            + ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponseHandler<Object>> loginUser(
            LoginUserRequest loginRequest) {

        try {

            WorkspaceAuth workspaceAuth = workspaceAuthRepository
                    .findByWorkspaceName(
                            loginRequest.getWorkspaceName())
                    .orElseThrow(() -> new BadRequestException(
                            "Workspace does not exist with name: "
                                    + loginRequest.getWorkspaceName()));

            UserAuth userAuth = userAuthRepository
                    .findWorkspaceUser(
                            loginRequest.getEmail(),
                            workspaceAuth.getWorkspaceId())
                    .orElseThrow(() -> new BadRequestException(
                            "User does not exist with email: "
                                    + loginRequest.getEmail()));

            if (!userAuth.getActive()) {

                throw new BadRequestException(
                        "User account is deactivated");
            }

            if (!passwordEncoder.matches(
                    loginRequest.getPassword(),
                    userAuth.getPassword())) {

                throw new BadRequestException(
                        "Invalid credentials");
            }

            String accessToken = jwtService.generateUserAccessToken(userAuth);

            String refreshToken = jwtService.generateUserRefreshToken(userAuth);

            ResponseCookie accessCookie = ResponseCookie.from(
                    "accessToken",
                    accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(15 * 60)
                    .sameSite("Strict")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from(
                    "refreshToken",
                    refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                    HttpStatus.OK,
                    "Login successful",
                    null);

            return ResponseEntity.ok()
                    .header(
                            "Set-Cookie",
                            accessCookie.toString())
                    .header(
                            "Set-Cookie",
                            refreshCookie.toString())
                    .body(successResponse);

        } catch (BadRequestException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new BadRequestException(
                    "An unexpected error occurred while processing your request: "
                            + ex.getMessage());
        }
    }

}