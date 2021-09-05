<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Calendario</title>

    <style type="text/css">

        img { max-width: 600px; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic;}
        a img { border: none; }
        table { border-collapse: collapse !important; }
        #outlook a { padding:0; }
        .ReadMsgBody { width: 100%; }
        .ExternalClass {width:100%;}
        .backgroundTable {margin:0 auto; padding:0; width:100%;!important;}
        table td {border-collapse: collapse;}
        .ExternalClass * {line-height: 115%;}


        td {
            font-family: Arial, sans-serif;
            color: #5e5e5e;
            font-size: 16px;
            text-align: left;
        }

        body {
            -webkit-font-smoothing:antialiased;
            -webkit-text-size-adjust:none;
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

        .grey-header {
            background-color: #dedede;
            padding: 5px;
            font-weight: bold;
        }

        .white-header {
            background-color: #f3f3f3;
            padding: 5px;
        }


    </style>

    <style type="text/css" media="screen">
        @media screen {
            @import url(http://fonts.googleapis.com/css?family=PT+Sans:400,700);
            * {
                font-family: 'PT Sans', 'Helvetica Neue', 'Arial', 'sans-serif' !important;
            }
        }
    </style>

    <style type="text/css" media="only screen and (max-width: 599px)">
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
                padding: 0 25px!important;
                padding-bottom: 25px !important;
            }

            *[class*="mobile-hide"] {
                display: none !important;
                width: 0 !important;
                height: 0 !important;
                line-height: 0 !important;
                font-size: 0 !important;
            }

            td[class="mobile-block"] {
                display: block !important;
                width: 260px !important;
            }


        }
    </style>
</head>
<body  offset="0" class="body" style="padding:0; margin:0; display:block; background:#ffffff; -webkit-text-size-adjust:none" bgcolor="#ffffff">
<table align="center" cellpadding="0" cellspacing="0" width="100%" height="100%">
    <tr>
        <td align="center" valign="top" style="background-color:#ffffff" width="100%">

            <center>
                <table cellspacing="0" cellpadding="0" width="600" class="w320">
                    <tr>
                        <td align="center" valign="top">

                            <table cellspacing="0" cellpadding="0" class="force-full-width" bgcolor="#232925">
                                <tr>
                                    <td class="computer-image">
                                        <br>
                                        <br class="mobile-hide" />
                                        <img style="display:block; margin-bottom: 40px;" width="224" height="213" src="https://aka-cdn.uce.edu.ec/ares/tmp/SIIU/anuncios/sello_400.png" alt="sello">
                                    </td>
                                    <td style="color: #ffffff;" class="header-text">
                                        <br>
                                        <br>
                                        <p style="font-size: 19px; text-align: center;">Sistema de Gestión de Vacunas U.C.E</p><br><br>
                                        Este email tiene el propósito de informar tus horarios de vacunación.
                                        <br>
                                    </td>
                                </tr>
                            </table>


                            <table class="force-full-width" cellspacing="0" cellpadding="30" bgcolor="#ebebeb">
                                <tr>
                                    <td class="mobile-block" width="325">
                                        <table cellspacing="0" cellpadding="0" width="100%">
                                            <tr>
                                                <td>
                                                    <table cellspacing="0" cellpadding="0" width="100%">
                                                        <tr>
                                                            <td class="grey-header">
                                                                Fase
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="white-header">
                                                                ${fase}
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <table cellspacing="0" cellpadding="0" width="100%">
                                                        <tr>
                                                            <td class="grey-header">
                                                                Centro Vacunación
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="white-header">
                                                                ${centroVacunacion}
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <table cellspacing="0" cellpadding="0" width="100%">
                                                        <tr>
                                                            <td class="grey-header">
                                                                Fecha
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="white-header">
                                                                ${fechaInicio} hasta ${fechaFinal} de 8:00 am - 16:00 pm
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>


                            <table cellspacing="0" cellpadding="0" class="force-full-width" bgcolor="#ebebeb">
                                <tr>
                                    <td style="padding: 0 30px;">
                                    <a href="https://sgvuce.herokuapp.com/login">Accede a la plataforma
                                        desde aquí.</a><br/><br/>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 0 30px; padding-bottom: 30px;">
                                        Toma las debidas precauciones. Respeta el distanciamiento.<br><br>
                                        Saludos,<br>
                                        Team Java
                                    </td>
                                </tr>
                            </table>


                            <table class="force-full-width" cellspacing="0" cellpadding="20" bgcolor="#2b934f">
                                <tr>
                                    <td style="background-color:#2b934f; color:#ffffff; font-size: 14px; text-align: center;">
                                        © 2021 All Rights Reserved - Alpala Saúl | Erazo Doménica | Jara Samantha | Díaz Erick
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