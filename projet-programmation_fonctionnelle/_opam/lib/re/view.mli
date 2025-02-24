(** A view of the top-level of a regex. This type is unstable and may change *)
type t =
  | Set of Cset.t
  | Sequence of Ast.t list
  | Alternative of Ast.t list
  | Repeat of Ast.t * int * int option
  | Beg_of_line
  | End_of_line
  | Beg_of_word
  | End_of_word
  | Not_bound
  | Beg_of_str
  | End_of_str
  | Last_end_of_line
  | Start
  | Stop
  | Sem of Automata.Sem.t * Ast.t
  | Sem_greedy of Automata.Rep_kind.t * Ast.t
  | Group of string option * Ast.t
  | No_group of Ast.t
  | Nest of Ast.t
  | Case of Ast.t
  | No_case of Ast.t
  | Intersection of Ast.t list
  | Complement of Ast.t list
  | Difference of Ast.t * Ast.t
  | Pmark of Pmark.t * Ast.t

val view : Ast.t -> t
