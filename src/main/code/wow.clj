(ns code.wow)

(defmacro new-let [[var val & rest :as steps] end-expr]
  (if steps
    `((fn [~var] (new-let ~rest ~(seq end-expr))) ~val)
    end-expr))

(defmacro like-ref-let [bindings & body]
  (let [[names items]
         (let [l (partition-all 2 bindings)]
           [(map first l) (map second l)])]
    `((fn [[~@names]] (code.util/blob ~@body)) [~@items])))

;; TODO check even arguments etc.
(defmacro fatmamba [[var val & rest :as steps] end-expr]
  (if steps
    `(code.bind/then ~val (fn [~var] (fatmamba ~rest ~(seq end-expr))))
    end-expr))

