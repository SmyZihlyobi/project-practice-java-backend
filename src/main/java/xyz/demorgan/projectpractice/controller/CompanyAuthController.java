package xyz.demorgan.projectpractice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.demorgan.projectpractice.config.jwt.JwtTokenUtils;
import xyz.demorgan.projectpractice.service.CompanyService;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Slf4j
public class CompanyAuthController {
    PasswordEncoder passwordEncoder;
    CompanyRepository companyRepository;
    JwtTokenUtils jwtTokenUtils;
    AuthenticationManager authenticationManager;
    HttpServletRequest request;
    CompanyService companyService;

    final static String LOGIN = "/company/login";
    final static String APPROVE_COMPANY = "/company/approve";

    @PostMapping(APPROVE_COMPANY)
    public void approveCompany(@RequestParam Long companyId) {
        companyService.approveCompany(companyId);
    }
}
