(ns code.macros)

(defmacro then
  "Bind a `Prog` to a continuation. Returns a `Bind`. cont should return a `Bind` or `Prog`"
  [[var val & rest :as steps] end-expr]
  ;; {:pre [(even? (count steps))
  ;;        (or (code.bind/bind? val)
  ;;            (code.bind/prog? val))
  ;;        (fn? end-expr)]}
  (if steps
    `(code.bind/then ~val (fn [~var] (then ~rest ~(seq end-expr))))
    ;; TODO: end-expr has arity 0, could check for last var and pass
    `(~end-expr)))

(defmacro runner
  "Show `Prog` (or `Bind`). Returns an `Item`"
  ([x]
   `(runner ~x (fn [])))
  ([x y]
   `(code.bind/runner (then ~x  ~y))))

