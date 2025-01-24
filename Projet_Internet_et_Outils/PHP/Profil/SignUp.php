<?php
    $mail = $_POST["mail"];
    $mdp = $_POST["mdp"];
    $username = $_POST["username"];
    /*$pre_pays = $_POST["pays"];
    if (isset($pre_pays)){
        if ($pre_pays!="rien"){
            $pays=$pre_pays;
        }
    }*/
    $connexion = mysqli_connect("localhost", "root", "", "moosik");

    if (isset($mail) && isset($mdp) && isset($username) /*&& isset($pays)*/ && isset($connexion)){
        $exist = mysqli_query($connexion, "SELECT * FROM users WHERE mail='$mail';");
        if (mysqli_num_rows($exist)==0){
            $chiffre =md5($mdp);
            $resultat = mysqli_query($connexion, "INSERT INTO users (username,mail,mdp) VALUES('$username', '$mail','$chiffre');");
            if ($resultat) {
                $_SESSION["mail"]=$mail;
                header("location:../index.php");
                exit;
            }else{
                $erreur="Une erreur est survenue, veuillez réessayer";
                header("location:../../Html/Profil/SignUp.html?erreur=3");
                exit;
            }
        }else{
            $erreur = "Utilisateur existant";
            header("location:../../Html/Profil/SignUp.html?erreur=2");
            exit;
        }
    }
?>
