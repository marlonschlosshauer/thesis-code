(ns code.macros)

;; TODO: check even arguments etc
(defmacro then
  ([[var val & rest :as steps] end-expr]
  (if steps
    `(code.bind/then ~val (fn [~var] (then ~rest ~(seq end-expr))))
    ;; TODO: end-expr has arity 0, could check for last var and pass
    `(~end-expr))))

;; TODO check even arguments etc.
(defmacro runner
  ([x]
   (runner x (fn [])))
  ([x y]
   `(code.bind/runner (then ~x  ~y))))

