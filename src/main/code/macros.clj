(ns code.macros)

;; TODO: check even arguments etc.
;; TODO: bug: error on end of chain
;; TODO: make version with-out end-expr
(defmacro then [[var val & rest :as steps] end-expr]
  (if steps
    `(code.bind/then ~val (fn [~var] (then ~rest ~(seq end-expr))))
    end-expr))

;; TODO check even arguments etc.
(defmacro runner [x y]
  `(code.bind/runner (then ~x ~y)))
