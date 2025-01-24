<?php
session_start();
$connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connexion
if (isset($connexion)){
    if(isset($_POST["liker"])){
            $scroll=$_POST["idScroll"];
            $ajout=mysqli_query($connexion,"INSERT INTO likes (publication,user) VALUES ('{$_POST['publication']}','{$_POST['userid']}');");
            mysqli_query($connexion,"UPDATE posts SET likes = (SELECT COUNT(*) FROM likes WHERE likes.publication = '{$_POST['publication']}') WHERE posts.id = '{$_POST['publication']}'; ");
            header("Location:index.php$scroll");      
    }if(isset($_POST["unliker"])){
            $scroll=$_POST["idScroll"];
            $delete=mysqli_query($connexion,"DELETE FROM likes WHERE publication='{$_POST['publication']}' AND user='{$_POST['userid']}';");
            mysqli_query($connexion,"UPDATE posts SET likes = (SELECT COUNT(*) FROM likes WHERE likes.publication = '{$_POST['publication']}') WHERE posts.id = '{$_POST['publication']}'; ");
            header("Location:index.php$scroll");
}
}else{
    header("Location:index.php$scroll");
}
?>