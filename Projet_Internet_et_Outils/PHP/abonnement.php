<?php
session_start();
$connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connexion
if (isset($connexion)){
    if(isset($_POST["abonnement"])){
        $abonne=mysqli_query($connexion,"SELECT * FROM users WHERE mail='{$_SESSION["mail"]}';");
        if(mysqli_num_rows($abonne)>0){
            $row=mysqli_fetch_assoc($abonne);
            $abonnement = $row['abonnements'];
            $abonnes = $_POST['abonnes'];
            $id_abonne=$row["id"];
            $id_abonnement=$_POST["id_abonnement"];
            $user=$_POST["user"];
            $ajout=mysqli_query($connexion,"INSERT INTO abonnement (id_abonne,id_abonnement) VALUES ('$id_abonne','$id_abonnement');");
            $abonnement = ($abonnement+1);
            $abonnes = ($abonnes+1);
            mysqli_query($connexion,"UPDATE users SET abonnements = $abonnement  WHERE id = $id_abonne  ");
            mysqli_query($connexion,"UPDATE users SET abonnés = $abonnes  WHERE id = $id_abonnement ");
            if (mysqli_affected_rows($connexion)>0){
                header("Location:PageUserAutre.php?user=$user");
            }else{
                header("Location:index.php");
            }
        }else{
            header("Location:index.php");
        }
    }if(isset($_POST["desabonnement"])){
        $abonne=mysqli_query($connexion,"SELECT * FROM users WHERE mail='{$_SESSION["mail"]}';");
        if(mysqli_num_rows($abonne)>0){
            $row=mysqli_fetch_assoc($abonne);
            $abonnement = $row['abonnements'];
            $abonnes = $_POST['abonnes'];
            $id_abonne=$row["id"];
            $id_abonnement=$_POST["id_abonnement"];
            $user=$_POST["user"];
            $supr=mysqli_query($connexion,"DELETE FROM abonnement WHERE id_abonne='$id_abonne' AND id_abonnement='$id_abonnement';");
            $abonnement = ($abonnement-1);
            $abonnes = ($abonnes-1);
            mysqli_query($connexion,"UPDATE users SET abonnements = $abonnement  WHERE id = $id_abonne  ");
            mysqli_query($connexion,"UPDATE users SET abonnés = $abonnes  WHERE id = $id_abonnement ");
            if (mysqli_affected_rows($connexion)>0){
                header("Location:PageUserAutre.php?user=$user");
            }else{
                header("Location:index.php");
            }
        }else{
            header("Location:index.php");
        }
    }
}else{
    header("Location:index.php");
}
?>