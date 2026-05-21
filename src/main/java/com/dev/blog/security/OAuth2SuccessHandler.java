package com.dev.blog.security;

import com.dev.blog.entity.Donateur;
import com.dev.blog.entity.Utilisateur;
import com.dev.blog.repository.DonateurRepository;
import com.dev.blog.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;
    private final DonateurRepository donateurRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (!utilisateurRepository.existsByEmail(email)) {
            String[] parts = name != null ? name.split(" ", 2) : new String[]{"User", ""};
            Utilisateur u = utilisateurRepository.save(Utilisateur.builder()
                    .email(email)
                    .nom(parts[0])
                    .prenom(parts.length > 1 ? parts[1] : "")
                    .motDePasse("")
                    .role(Utilisateur.Role.DONATEUR)
                    .build());
            donateurRepository.save(Donateur.builder()
                    .utilisateur(u)
                    .typeOrganisation("Particulier")
                    .build());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtils.generateToken(userDetails);
        getRedirectStrategy().sendRedirect(request, response, "/index.html?token=" + token);
    }
}
