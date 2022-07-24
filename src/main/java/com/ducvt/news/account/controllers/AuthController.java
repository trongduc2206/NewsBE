package com.ducvt.news.account.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ducvt.news.account.payload.request.DecodeRequest;
import com.ducvt.news.account.repository.RoleRepository;
import com.ducvt.news.account.repository.UserRepository;
import com.ducvt.news.account.security.jwt.JwtUtils;
import com.ducvt.news.account.security.services.UserDetailsImpl;
import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.fw.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ducvt.news.account.models.ERole;
import com.ducvt.news.account.models.Role;
import com.ducvt.news.account.models.User;
import com.ducvt.news.account.payload.request.LoginRequest;
import com.ducvt.news.account.payload.request.SignupRequest;
import com.ducvt.news.account.payload.response.JwtResponse;
import com.ducvt.news.account.payload.response.MessageResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if(loginRequest.getType().equals("SYSTEM")) {
            Optional<User> user = userRepository.findByUsernameAndType(loginRequest.getUsername(), "SYSTEM");
            if (user.isPresent()) {
                if (user.get().getStatus() == 1) {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String jwt = jwtUtils.generateJwtToken(authentication);

                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(item -> item.getAuthority())
                            .collect(Collectors.toList());

                    return ResponseFactory.success(new JwtResponse(jwt,
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            roles));
                } else {
                    throw new BusinessLogicException(MessageEnum.LOCKED_ACCOUNT.getMessage());
                }
            } else {
                throw new BusinessLogicException(MessageEnum.WRONG_ACCOUNT.getMessage());
            }
        } else {
            Optional<User> userOptional = userRepository.findByEmailAndType(loginRequest.getEmail(), loginRequest.getType());
            if(userOptional.isPresent()) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());

                return ResponseFactory.success(new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
            } else {
                throw new BusinessLogicException(MessageEnum.WRONG_ACCOUNT.getMessage());
            }
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            if (signUpRequest.getType() != null && signUpRequest.getType().equals("FACEBOOK")) {
                User facebookUser = userRepository.findByThirdPartyIdAndType(signUpRequest.getThirdPartyId(), "FACEBOOK").get();
                return ResponseFactory.success(facebookUser.getId());
            } else if(signUpRequest.getType() != null && signUpRequest.getType().equals("GOOGLE")) {
                User googleUser = userRepository.findByThirdPartyIdAndType(signUpRequest.getThirdPartyId(), "GOOGLE").get();
                return ResponseFactory.success(googleUser.getId());
            } else {
                throw new BusinessLogicException(MessageEnum.DUPLICATE_USERNAME.getMessage());
            }
        }

//    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//      throw new BusinessLogicException((MessageEnum.DUPLICATE_EMAIL.getMessage()));
//    }

        // Create new user's account
        User user;
        if (signUpRequest.getPassword() != null && !signUpRequest.getPassword().isEmpty()) {
            user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));
        } else {
            user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
        }

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        if (signUpRequest.getThirdPartyId() != null) {
            user.setThirdPartyId(signUpRequest.getThirdPartyId());
        }
        if (signUpRequest.getType() != null && !signUpRequest.getType().isEmpty()) {
            user.setType(signUpRequest.getType());
        } else {
            user.setType("SYSTEM");
        }

        user.setRoles(roles);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userRepository.save(user);

        return ResponseFactory.success(user.getId());
    }

    @PostMapping(value="/decode")
    public ResponseEntity decode(@RequestBody DecodeRequest request) {
        return ResponseFactory.success(jwtUtils.decode(request.getJwt()));
    }
}
