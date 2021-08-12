package uce.proyect.security;

import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/*
Esta clase se define para enviar los mensajes de no autorizado al momento de acceder a los recursos
y sus credenciales no sean validas o que no las haya ingresado
* */
@Component
public class AuthenticationEntryPointConfig extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic Realm - ".concat(getRealmName()));
        response.setStatus(SC_UNAUTHORIZED); // Defino el estado en este caso 401
        response.setContentType("application/json"); // Defino el contendio que va a consumir
        var writer = response.getWriter();
        var jsonObject = new JSONObject(); // Con la libreria org.json permite generar JSON sin maps, verificar si es la mejor manera o es mejor maps
        writer.println(jsonObject.put("mensaje", "HTTP Status 401 - ".concat(authException.getMessage())));
    }

    @Override
    public void afterPropertiesSet() { // Defino el nombre que usará la cabecera
        setRealmName("S.G.VACUNAS - UCE");
        super.afterPropertiesSet();
    }
}
