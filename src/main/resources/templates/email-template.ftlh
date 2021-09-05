<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Credenciales de Usuario</title>
    <!-- Designed by https://github.com/kaytcat -->
    <!-- Header image designed by Freepik.com -->

    <style type="text/css">
        /* Take care of image borders and formatting */

        img {
            max-width: 600px;
            outline: none;
            text-decoration: none;
            -ms-interpolation-mode: bicubic;
        }

        a img {
            border: none;
        }

        table {
            border-collapse: collapse !important;
        }

        #outlook a {
            padding: 0;
        }

        .ReadMsgBody {
            width: 100%;
        }

        .ExternalClass {
            width: 100%;
        }

        .backgroundTable {
            margin: 0 auto;
            padding: 0;
            width: 100%;
        !important;
        }

        table td {
            border-collapse: collapse;
        }

        .ExternalClass * {
            line-height: 115%;
        }


        /* General styling */

        td {
            font-family: Arial, sans-serif;
            color: #5e5e5e;
            font-size: 16px;
            text-align: left;
        }

        body {
            -webkit-font-smoothing: antialiased;
            -webkit-text-size-adjust: none;
            width: 100%;
            height: 100%;
            color: #5e5e5e;
            font-weight: 400;
            font-size: 16px;
        }


        h1 {
            margin: 10px 0;
        }

        a {
            color: #2b934f;
            text-decoration: none;
        }


        .body-padding {
            padding: 0 75px;
        }


        .force-full-width {
            width: 100% !important;
        }

        .icons {
            text-align: right;
            padding-right: 30px;
        }

        .logo {
            text-align: left;
            padding-left: 30px;
        }

        .computer-image {
            padding-left: 30px;
        }

        .header-text {
            text-align: left;
            padding-right: 30px;
            padding-left: 20px;
        }

        .header {
            color: #232925;
            font-size: 24px;
        }


    </style>

    <style type="text/css" media="screen">
        @media screen {
            @import url(http://fonts.googleapis.com/css?family=PT+Sans:400,700);
            /* Thanks Outlook 2013! */
            * {
                font-family: 'PT Sans', 'Helvetica Neue', 'Arial', 'sans-serif' !important;
            }
        }
    </style>

    <style type="text/css" media="only screen and (max-width: 599px)">
        /* Mobile styles */
        @media only screen and (max-width: 599px) {

            table[class*="w320"] {
                width: 320px !important;
            }

            td[class*="icons"] {
                display: block !important;
                text-align: center !important;
                padding: 0 !important;
            }

            td[class*="logo"] {
                display: block !important;
                text-align: center !important;
                padding: 0 !important;
            }

            td[class*="computer-image"] {
                display: block !important;
                width: 230px !important;
                padding: 0 45px !important;
                border-bottom: 1px solid #e3e3e3 !important;
            }


            td[class*="header-text"] {
                display: block !important;
                text-align: center !important;
                padding: 0 25px !important;
                padding-bottom: 25px !important;
            }

            *[class*="mobile-hide"] {
                display: none !important;
                width: 0 !important;
                height: 0 !important;
                line-height: 0 !important;
                font-size: 0 !important;
            }


        }
    </style>
</head>
<body offset="0" class="body"
      style="padding:0; margin:0; display:block; background:#ffffff; -webkit-text-size-adjust:none" bgcolor="#ffffff">
<table align="center" cellpadding="0" cellspacing="0" width="100%" height="100%">
    <tr>
        <td align="center" valign="top" style="background-color:#ffffff" width="100%">

            <center>
                <table cellspacing="0" cellpadding="0" width="600" class="w320">
                    <tr>
                        <td align="center" valign="top">

                            <table class="force-full-width" cellspacing="0" cellpadding="0" bgcolor="#232925">
                                <tr>
                                    <td class="icons">
                                        <br>
                                        <a href="https://github.com/erickdp/S.G.Vacunas-UCE"><img width="40" height="40" src="../static/img/git.png"></a>
                                    </td>
                                </tr>
                            </table>

                            <table cellspacing="0" cellpadding="0" class="force-full-width" bgcolor="#232925">
                                <tr>
                                    <td class="computer-image">
                                        <br class="mobile-hide"/>
                                        <img style="display:block;" width="220" height="213"
                                             src="../static/img/sello_400.png" alt="hello">
                                        <br/>
                                    </td>
                                    <td style="color: #ffffff;" class="header-text">
                                        <div>
                                            <p style="font-size: 21px;text-align: center;">Sistema de Gestión de Vacunas U.C.E</p><br><br>
                                        </div>
                                        Un cordial saludo y bienvenido a la plataforma, aquí usted podrá
                                        <ul>
                                            <li>Consultar sus datos de vacunación</li>
                                            <li>Acceder a la vacunación voluntaria</li>
                                        </ul>
                                    </td>
                                </tr>
                            </table>

                            <table class="force-full-width" cellspacing="0" cellpadding="30" bgcolor="#ebebeb">
                                <tr>
                                    <td>
                                        <table cellspacing="0" cellpadding="0" class="force-full-width">
                                            <tr>
                                                <td>
                                                    <span class="header">Sus credenciales de acceso:</span><br>
                                                    Usuario: <a href="#">${nombreUsuario}</a> <br>
                                                    Contraseña: <a href="#">${password}</a> <br><br>
                                                    <span class="header">Preguntas?</span><br>
                                                    Contactate con nosotros! <a href="mailto:sgvuce@gmail.com" >
                                                    En cualquier momento.</a><br/><br/>
                                                    Que tengas una excelente experiencia,<br/>
                                                    Team Java.
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>


                            <table class="force-full-width" cellspacing="0" cellpadding="20" bgcolor="#2b934f">
                                <tr>
                                    <td style="background-color:#2b934f; color:#ffffff; font-size: 12px; text-align: center;">
                                        © 2021 All Rights Reserved - Alpala Saúl | Erazo Doménica | Jara Samantha | Díaz Erick C.E.O
                                    </td>
                                </tr>
                            </table>


                        </td>
                    </tr>
                </table>

            </center>
        </td>
    </tr>
</table>
</body>
</html>