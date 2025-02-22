package xyz.demorgan.projectpractice.config.jwt;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    CompanyRepository companyRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Company user = companyRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("User Not Found with email: " + email));

        return new User(
                user.getEmail(),
                user.getPassword(),
                Stream.of("ROLE_COMPANY")
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
