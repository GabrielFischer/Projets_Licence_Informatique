(* $Id$
 * ----------------------------------------------------------------------
 *
 *)

let config_file = "/Users/johnson/Desktop/projet-pf5-2024-25/projet-pf5-2425/_opam/lib/findlib.conf";;

let ocaml_stdlib = "/Users/johnson/.opam/4.14.1/lib/ocaml";;

let ocaml_ldconf = Filename.concat ocaml_stdlib "ld.conf";;

let ocaml_has_autolinking = true;;

let libexec_name = "stublibs";;

let system = "macosx";;
(* - "mingw", "mingw64", "win32", "cygwin", "linux_elf", ... *)

let dll_suffix =
  match Sys.os_type with
    | "Unix"  | "BeOS"   -> ".so"
    | "Win32" | "Cygwin" -> ".dll"
    | "MacOS"            -> ""        (* don't know *)
    | _ -> failwith "Unknown Sys.os_type"
;;
