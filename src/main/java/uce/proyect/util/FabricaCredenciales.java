package uce.proyect.util;

import uce.proyect.models.User;

import java.util.*;
import java.util.stream.Collectors;

public class FabricaCredenciales {

    public static final String EST = "ESTUDIANTE";
    public static final String ADMIN = "ADMINISTRADOR";
    public static final String HC = "CONTROLADOR";

    private static final String[] ADMINISTRADOR = {"ROLE_ADMIN", "ROLE_HC", "ROLE_USER"};
    private static final String[] CONTROLADOR = {"ROLE_HC", "ROLE_USER"};
    private static final String[] ESTUDIANTE = {"ROLE_USER"};

    public static User generarUsuario(String nombres, String apellidos, String cargo) {
        var nombresArray = nombres.toLowerCase().split(" "); // Decirles que validen que no haya espacios en blanco a los lados de los nombres
        var apellidosArray = apellidos.toLowerCase().split(" ");
        var nombreUsuario = new StringBuilder();
        var contrasena = new StringBuilder();

        var random = new Random();

        nombreUsuario
                .append(nombresArray[0].charAt(0))
                .append(nombresArray[1].charAt(0))
                .append(apellidosArray[0])
                .append(apellidosArray[1].charAt(0))
                .append(random.nextInt(9) + 1)
                .append(random.nextInt(9) + 1);

        contrasena
                .append(nombresArray[0].charAt(0))
                .append(apellidosArray[0]);

        var user = new User();
        user.setNombreUsuario(nombreUsuario.toString());
        user.setContrasena(contrasena.toString());

        switch (cargo) {
            case EST:
                user.setRoles(Arrays.stream(ESTUDIANTE).collect(Collectors.toList()));
                break;
            case ADMIN:
                user.setRoles(Arrays.stream(ADMINISTRADOR).collect(Collectors.toList()));
                break;
            case HC:
                user.setRoles(Arrays.stream(CONTROLADOR).collect(Collectors.toList()));
                break;
        }

        return user;
    }

    public static String generarIdentificador() {
        var r = new Random();
        var codigo = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            codigo.append(r.nextInt(10) + 1);
        }
        for (int i = 0; i < 3; i++) {
            codigo.append((char) (r.nextInt(26) + 'A'));
        }
        return codigo.toString();
    }

}
