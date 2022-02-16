(ns code.macros
  (:require #?(:cljs [code.util :as u])
            #?(:clj [code.util :as u])))

(defmacro new-let [[var val & rest :as steps] end-expr]
  (if steps
    `((fn [~var] (run-let ~rest ~(seq end-expr))) ~val)
    end-expr))

(defmacro like-ref-let [bindings & body]
  (let [[names items]
         (let [l (partition-all 2 bindings)]
           [(map first l) (map second l)])]
    `((fn [[~@names]] ~@body) [~@items])))
