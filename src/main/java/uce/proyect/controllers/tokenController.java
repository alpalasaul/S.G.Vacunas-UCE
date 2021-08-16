package uce.proyect.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uce.proyect.service.agreementImp.JwtService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/token")
public class tokenController {

    @Autowired
    private JwtService jwtService;

    @PreAuthorize("authenticated")
    @PostMapping("/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails) {
        List<String> roleList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        return jwtService.createToken(userDetails.getUsername(), roleList);
    }

}
