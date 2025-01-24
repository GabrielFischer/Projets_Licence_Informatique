<?php
session_start();
$connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connexion
if (isset($connexion)){
    if(isset($_POST["supprimer"])){
            $scroll=$_POST["idScroll"];
            $suppression=mysqli_query($connexion,"DELETE FROM posts WHERE id='{$_POST['publication']}';");
            mysqli_query($connexion,"DELETE FROM likes WHERE publication ='{$_POST['publication']}';");
            if ($scroll==="pageProfil.php"){
                header("Location:pageProfil.php");
                exit;
            }else{
                header("Location:index.php$scroll");
                exit;
            }           
    }
    header("Location:index.php");
    exit;
}else{
    header("Location:LogIn.php");
    exit;
}
?>