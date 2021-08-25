package uce.proyect.util;

import uce.proyect.models.Carnet;
import uce.proyect.models.User;

import java.time.LocalDate;
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
        var nombresArray = nombres.trim().toLowerCase().split(" "); // Decirles que validen que no haya espacios en blanco a los lados de los nombres
        var apellidosArray = apellidos.trim().toLowerCase().split(" ");
        var nombreUsuario = new StringBuilder();
        var contrasena = new StringBuilder();

        var random = new Random();

        nombreUsuario.append(nombresArray[0].charAt(0));
        if (nombresArray.length > 1) {
            nombreUsuario.append(nombresArray[1].charAt(0));
        }
        nombreUsuario
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

    public static Carnet generarCarnet(String nombreUsuario) {
        var carnet_default = new Carnet();
        carnet_default.setEstudiante(nombreUsuario);
        carnet_default.setCentroVacunacion("N/A");
        carnet_default.setFechaPrimeraDosis(null);
        carnet_default.setFechaSegundasDosis(null);
        carnet_default.setLoteDosisDos("N/A");
        carnet_default.setLoteDosisUno("N/A");
        carnet_default.setNombreVacuna("N/A");
        carnet_default.setPrimeraDosis(false);
        carnet_default.setSegundaDosis(false);
        carnet_default.setVacunadorPrimeraDosis("N/A");
        carnet_default.setVacunadorSegundaDosis("N/A");
        carnet_default.setInoculacionVoluntaria(true); // Todos esmpeizan con inoculacion verdadera, pero en el caso de no querer mabia a falso
        return carnet_default;
    }

}
