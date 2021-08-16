package uce.proyect.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uce.proyect.service.agreementImp.JwtService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sgv")
@CrossOrigin(origins = "*")
public class tokenController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal UserDetails userDetails) {
        List<String> roleList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        var test = new JSONObject();
        test.put("user", userDetails.getUsername());
        test.put("token", jwtService.createToken(userDetails.getUsername(), roleList));
        return new ResponseEntity<>(test.toMap(), HttpStatus.CREATED);
    }
}
