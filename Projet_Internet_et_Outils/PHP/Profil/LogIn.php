<?php
    session_start();
    $mail = $_POST["mail"];
    $mdp = $_POST["mdp"];
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    
    
    if (isset($mail) && isset($mdp) && isset($connexion)){
        $exist = mysqli_query($connexion, "SELECT * FROM users WHERE mail='$mail';");
        echo $mail;
        echo $mdp;
        print_r($exist);
        if (mysqli_num_rows($exist)!=0){
            $chiffre =md5($mdp);
            if (mysqli_num_rows($exist)!=0){
                $mdp_chiffre = mysqli_fetch_assoc($exist);
                $mdp_chiffre = $mdp_chiffre['mdp'];
            }
            $resultat = $chiffre === $mdp_chiffre;
            echo $resultat;
            if ($resultat) {
                $_SESSION["mail"]=$mail;
                header("location:../index.php"); // change automatiquement la page vers index.php
                exit;
            }else{
                $erreur = "Utilisateur introuvable ou mot de passe incorrect";
                header("location:../../Html/Profil/LogIn.html?erreur=1"); // change automatiquement la page vers LogIn.php
                exit;
            }
        }else{
            $erreur = "Utilisateur introuvable ou mot de passe incorrect";
                header("location:../../Html/Profil/LogIn.html?erreur=1"); 
                exit;
        }
    }
    ?>