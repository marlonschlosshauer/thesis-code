(ns code.macros)

(defmacro then
  "Bind a `Prog` to a continuation. Returns a `Bind`. cont should return a `Bind` or `Prog`"
  [[var val & rest :as steps] end-expr]
  (if steps
    `(code.bind/then ~val (fn [~var] (then ~rest ~(seq end-expr))))
    `(~end-expr)))

(defmacro runner
  "Show `Prog` (or `Bind`). Returns an `Item`"
  ([x]
   `(runner ~x (fn [])))
  ([x y]
   `(code.bind/runner (then ~x  ~y))))

