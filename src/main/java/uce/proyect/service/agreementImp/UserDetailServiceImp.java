package uce.proyect.service.agreementImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.repositories.UserRepository;

import java.util.stream.Collectors;

import static uce.proyect.util.ConservarRoles.*;

@Service
public class UserDetailServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    // Al inciar sesion se delega el control a este método y si encuentra al usuario hace el proceso de roles
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        var user = this.userRepository.findByNombreUsuario(nombreUsuario).orElseThrow();
        ROLE_MAXIMO = user.getRoles().get(0);
        return new org.springframework.security.core.userdetails.User(
                user.getNombreUsuario(),
                user.getContrasena(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) // le paso los roles para que cree la nueva lista
        );
    }
}
