package com.Kartik.notiflow.WorkspaceAuth;

import com.Kartik.notiflow.Common.Exception.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Kartik.notiflow.Common.Exception.ConflictException;
import com.Kartik.notiflow.Common.Response.ApiResponseHandler;
import com.Kartik.notiflow.Common.Response.ResponseBuilder;
import com.Kartik.notiflow.Config.JwtService;
import com.Kartik.notiflow.WorkspaceAuth.Dto.LoginRequest;
import com.Kartik.notiflow.WorkspaceAuth.Dto.LoginResponse;
import com.Kartik.notiflow.WorkspaceAuth.Dto.RegisterRequest;
import com.Kartik.notiflow.WorkspaceAuth.Dto.RegisterResponse;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class WorkspaceAuthService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiry}")
    private long ACCESS_TOKEN_EXPIRY;

    @Value("${jwt.refresh-token-expiry}")
    private long REFRESH_TOKEN_EXPIRY;

    private final WorkspaceAuthRepository workspaceAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public ResponseEntity<ApiResponseHandler<Object>> createWorkspace(
                    RegisterRequest registerRequest) {
            try {
                    Boolean workspaceExists = workspaceAuthRepository
                                    .findByWorkspaceName(registerRequest.getWorkspaceName())
                                    .isPresent();
                    if (workspaceExists) {
                            throw new ConflictException(
                                            "Workspace already exists with name: "
                                                            + registerRequest.getWorkspaceName());
                    }
                    Boolean userExists = workspaceAuthRepository
                                    .findByUsername(registerRequest.getUsername())
                                    .isPresent();
                    if (userExists) {
                            throw new ConflictException(
                                            "Username already exists: "
                                                            + registerRequest.getUsername());
                    }
                    WorkspaceAuth workspaceAuth = modelMapper.map(registerRequest, WorkspaceAuth.class);
                    workspaceAuth.setPassword(
                                    passwordEncoder.encode(registerRequest.getPassword()));
                    WorkspaceAuth savedWorkspace = workspaceAuthRepository.save(workspaceAuth);
                    RegisterResponse registerResponse = modelMapper.map(savedWorkspace, RegisterResponse.class);
                    ApiResponseHandler<Object> successResponse = ResponseBuilder.success(
                                    HttpStatus.CREATED,
                                    "Workspace created successfully",
                                    registerResponse);
                    return ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .body(successResponse);
            } catch (ConflictException ex) {
                    throw new BadRequestException(
                                    "Database error: " + ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                    throw new BadRequestException(
                                    "Database error: Could not save workspace due to invalid data constraint.");
            } catch (BadRequestException ex) {
                    throw ex;
            } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new BadRequestException(
                                    "An unexpected error occurred while processing your request: "
                                                    + ex.getMessage());
            }
    }
    public ResponseEntity<ApiResponseHandler<Object>> loginWorkspace(
        LoginRequest loginRequest) {

    try {

        WorkspaceAuth workspaceAuth = workspaceAuthRepository
                .findByWorkspaceName(loginRequest.getWorkspaceName())
                .orElseThrow(() -> new BadRequestException(
                        "Workspace does not exist with name: "
                                + loginRequest.getWorkspaceName()));

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                workspaceAuth.getPassword())) {

            throw new BadRequestException("Invalid credentials");
        }

        String accessToken =
                jwtService.generateWorkspaceAccessToken(workspaceAuth);

        String refreshToken =
                jwtService.generateWorkspaceRefreshToken(workspaceAuth);

        // ResponseCookie accessCookie =
        //         ResponseCookie.from("accessToken", accessToken)
        //                 .httpOnly(true)
        //                 .secure(false) // true in production HTTPS
        //                 .path("/")
        //                 .maxAge(15 * 60)
        //                 .sameSite("Strict")
        //                 .build();

        // ResponseCookie refreshCookie =
        //         ResponseCookie.from("refreshToken", refreshToken)
        //                 .httpOnly(true)
        //                 .secure(false) // true in production HTTPS
        //                 .path("/")
        //                 .maxAge(7 * 24 * 60 * 60)
        //                 .sameSite("Strict")
        //                 .build();

                LoginResponse loginResponse =
                        new LoginResponse(accessToken, refreshToken);

                ApiResponseHandler<Object> successResponse =
                        ResponseBuilder.success(
                                HttpStatus.OK,
                                "Login successful",
                                loginResponse);

                return ResponseEntity.ok(successResponse);

        } catch (BadRequestException ex) {

                throw ex;

        } catch (Exception ex) {

                throw new BadRequestException(
                        "An unexpected error occurred while processing your request: "
                                + ex.getMessage());
                }
        }
}