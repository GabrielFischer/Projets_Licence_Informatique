(* Code de la Section 3 du projet. *)

type coord2D = {
    x : float;
    y : float
  }
type point = coord2D
type vector = coord2D
type angle = float

let translate (v : vector) (p : point) : point =
  {x = p.x +. v.x ;y = p.y +. v.y};;
  (*ici les accolades ainsi que x et y sont obligatoires pour que ce soit bien un point qui soit retourné*)

let rad_of_deg (a : angle) : angle =
  a*. Float.pi /. 180.  ;;
  (* 1 pi en radian vaut 180 degres donc produit en croix pour trouver x pi radian en degrés*)

let deg_of_rad (a : angle) : angle =
  a*.180. /. Float.pi ;;
    (* 1 pi en radian vaut 180 degres donc produit en croix pour trouver x degrés en pi radian*)

let rotate (c : point) (alpha : angle) (p : point) : point =
  {x = c.x +. (p.x -. c.x)*.cos(rad_of_deg(alpha)) -. (p.y -. c.y)*.sin(rad_of_deg(alpha)) ; y = c.y +. (p.x -. c.x)*.sin(rad_of_deg(alpha)) +. (p.y -. c.y)*.cos(rad_of_deg(alpha))};;
  (*On n'oublie pas de convertir alpha, qui est exprimé en degrés, en radians afin d'appeler cos et sin sur alpha*)

type transformation =
  Translate of vector
| Rotate of point * angle

let transform (t : transformation) (p : point) : point =
  match t with
  |Translate vect-> translate vect p 
  |Rotate (pnt,agl) -> rotate pnt agl p
  (*Applique soit la fonction translate soit rotate en fonction de la structure de t*) 
;;

type rectangle = {
    x_min : float;
    x_max : float;
    y_min : float;
    y_max : float
  }

let in_rectangle (r : rectangle) (p : point) : bool =
  if(p.x >= r.x_min && p.x <= r.x_max && p.y >= r.y_min && p.y <= r.y_max) then true else false;;
  (*ici, il s'agit simplement de vérifier si les coordonnées du point respectent les intervalles imposés par les coordonnées du rectangle*)

let corners (r :rectangle) : point list =
  let left_up = {x=r.x_min; y=r.y_max} in
  let right_up = {x=r.x_max; y=r.y_max} in
  let left_down = {x=r.x_min; y=r.y_min} in
  let right_down = {x=r.x_max; y=r.y_min} in
  [left_up; right_up; left_down; right_down] ;;
  (*On determine les 4 coins du rectangle puis on les retourne dans une liste*)
  
let rectangle_of_list (pl : point list) : rectangle = 
  (*On utilise une fonction auxiliaire récursive afin de parcourir toute la liste à la recherche des points x y minimum et maximum
  , le principe de la fonction récursive recherchePoints est de parcourir la liste tout en stockant les coordonnées x et y du point courant 
  s'il sont plus petits ou plus grands que les point minimum/maximum stockés*)
  let rec recherchePoints pl xMinimum xMaximum yMinimum yMaximum = match pl with
  |[] ->{ x_min = xMinimum; x_max = xMaximum; y_min = yMinimum; y_max = yMaximum}(*On renvoie ici le rectangle le plus petit contenant tous les points de la liste*)
  |{x;y}:: reste -> recherchePoints reste (min x xMinimum) (max x xMaximum) (min y yMinimum) (max y yMaximum) in
  match pl with
  |[] -> failwith "Liste de points vides " (*optionnel vu que la liste est supposée non vide par le sujet*)
  |prems::reste -> recherchePoints reste prems.x prems.x prems.y prems.y