package xyz.demorgan.projectpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.demorgan.projectpractice.service.CompanyService;
import xyz.demorgan.projectpractice.store.dto.input.CompanyLoginDto;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Tag(name = "Company auth controller", description = "Controller for company authentication")
@Slf4j
@Validated
public class CompanyAuthController {
    CompanyService companyService;

    final static String LOGIN = "/company/login";
    final static String APPROVE_COMPANY = "/company/approve";
    final static String CHANGE_PASSWORD = "/company/change-password";

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Approve company", description = "Approve company by id to allow it to login")
    @PostMapping(APPROVE_COMPANY)
    public ResponseEntity<?> approveCompany(@RequestParam Long companyId) {
        companyService.approveCompany(companyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(LOGIN)
    @Operation(summary = "Login company", description = "Login company by email and password")
    public ResponseEntity<?> login(@RequestBody @Valid CompanyLoginDto companyLoginDto) {
        return companyService.login(companyLoginDto);
    }

    @PostMapping(CHANGE_PASSWORD)
    @Operation(summary = "Change password", description = "Change password for company by email")
    public ResponseEntity<?> changePassword(@RequestParam String email) {
        companyService.changePassword(email);
        return ResponseEntity.ok().build();
    }
}
