package tn.tradenet.elfatoora.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.api.dto.LoginRequest;
import tn.tradenet.elfatoora.api.dto.UserResponse;
import tn.tradenet.elfatoora.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /**
     * Simple login: validates username/password, returns user info. Frontend then uses same credentials as HTTP Basic for other requests.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            var user = userRepository.findByUsername(auth.getName()).orElseThrow();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "user", UserResponse.builder()
                    .username(user.getUsername())
                    .displayName(user.getDisplayName())
                    .build()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return userRepository.findByUsername(principal.getUsername())
            .map(u -> ResponseEntity.ok(UserResponse.builder()
                .username(u.getUsername())
                .displayName(u.getDisplayName())
                .build()))
            .orElse(ResponseEntity.status(401).build());
    }
}
