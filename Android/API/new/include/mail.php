<?php
$styleBtnGrant = "display: inline-block;  padding: 12px 24px;  margin-bottom: 20px;  font-size: 14px;
    font-weight: normal;  line-height: 1.42857143;  text-align: center;  white-space: nowrap;
    vertical-align: middle;  -ms-touch-action: manipulation;      touch-action: manipulation;  cursor: pointer;
    -webkit-user-select: none;     -moz-user-select: none;      -ms-user-select: none;
    user-select: none;  background-image: none;  border: 1px solid transparent;  border-radius: 4px;
    color: #fff;  background-color: #337ab7;  border-color: #2e6da4;text-decoration : none;";
$styleBtnNot = "display: inline-block;  padding: 12px 24px;  margin-bottom: 20px;  font-size: 14px;
font-weight: normal;  line-height: 1.42857143;  text-align: center;  white-space: nowrap;  vertical-align: middle;
-ms-touch-action: manipulation;      touch-action: manipulation;  cursor: pointer;  -webkit-user-select: none;
-moz-user-select: none;      -ms-user-select: none;          user-select: none;  background-image: none;
border: 1px solid transparent;  border-radius: 4px;color: #fff;background-color: #d9534f;border-color: #d43f3a;
text-decoration : none;";
$linkGrant = "ok";
$linkNot = "nok";
$hostName="http://www.intellifridge.ovh/";
$name = "Rémy";
$mail = "his@name.com";
$msg = "Hello";
$title = "Inscription";
$link = "http://app.intellifridge.ovh/inscription/dfhezfjsgs4gsg4s6gs";
$content = "<div align='left' style='color: #474747;' class='article-content'>
                <p> Bonjour $name,</p>
                <p>Bienvenue chez IntelliFridge.</p>
                <p>
                    Nous vous remercions de vous être inscrit chez nous.
                </p>
                <p>
                    Votre adresse mail est : <a href='#'>". strip_tags($mail) . "</a>.
                </p>
                <p>
                    Veuillez cliquer sur ce lien pour confirmer votre inscription :
                </p>
                <blockquote style='border-left: medium solid grey;padding-left : 10px;font-size : 100%;background-color : #d1d1d1;padding:10px;'>
                    <a href='$link'>$link</a>
                </blockquote>
                <br/>
                <p>Cordialement,</p>
                <p>L'équipe d'IntelliFridge</p>
                <br/>
            </div>";
$mail = "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background-color:rgb(42, 55, 78);font-family: Georgia'>
    <tbody>
    <tr>
        <td align='center' bgcolor='#2A374E'>
            <table cellpadding='0' cellspacing='0' border='0'>
                <tbody>
                <tr>
                    <td class='w640' width='640' height='10'></td>
                </tr>
                <tr>
                    <td class='w640' width='640' height='10'></td>
                </tr>


                <!-- entete -->
                <tr class='pagetoplogo'>
                    <td class='w640' width='640'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0' bgcolor=''#d9534f'>
                            <tbody>
                            <tr>
                                <td style='text-align: center' class='w30' width='30'><a style='color:#255D5C;' href='$hostName'><img style='width: 70%' src='$hostName/view/img/logo/intelliFridge.png' alt='intellifridge logo'></a></td>
                            </tr>
                            </tbody>
                        </table>
                        <br/>
                    </td>
                </tr>

                <!-- separateur horizontal -->
                <tr>
                    <td class='w640' width='640' height='1' bgcolor='white'></td>
                </tr>

                <!-- contenu -->
                <tr class='content'>
                    <td class='w640' width='640' style='background-color: white' bgcolor=''#ffffff'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0'>
                            <tbody>
                            <tr>
                                <td class='w30' width='30'></td>
                                <td class='w580' width='580'>
                                    <!-- une zone de contenu -->
                                    <table class='w580' width='580' cellpadding='0' cellspacing='0' border='0'>
                                        <tbody>
                                        <tr>
                                            <td class='w580' width='580'>
                                                <h2 style='color:#0E7693; font-size:30px; padding-top:12px;font-family: Lato Black, sans-serif'>
                                                    $title  </h2>
                                                    $content
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class='w580' width='580' height='1' bgcolor=''#c7c5c5'></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!-- fin zone -->


                                </td>
                                <td class='w30' width='30'></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>

                <!--  separateur horizontal de 15px de haut -->
                <tr>
                    <td class='w640' width='640' height='15' bgcolor=''#ffffff'></td>
                </tr>

                <!-- pied de page -->
                <tr class='pagebottom'>
                    <td class='w640' width='640'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0' bgcolor=''#c7c7c7'>
                            <tbody>
                            <tr>
                                <td colspan='5' height='10'></td>
                            </tr>
                            <tr>
                                <td class='w30' width='30'></td>
                                <td class='w580' width='580' valign='top'>
                                    <p align='right' class='pagebottom-content-left'>
                                        <a style='color:#255D5C;' href='$hostName'><span style='color:white;'>Notre Site web</span></a>
                                    </p>
                                </td>

                                <td class='w30' width='30'></td>
                            </tr>
                            <tr>
                                <td colspan='5' height='10'></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class='w640' width='640' height='60'></td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>";
echo $mail;