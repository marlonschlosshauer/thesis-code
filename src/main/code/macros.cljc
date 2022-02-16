(ns code.macros)

(defmacro new-let [[var val & rest :as steps] end-expr]
  (if steps
    `((fn [~var] (run-let ~rest ~(seq end-expr))) ~val)
    end-expr))

(comment
  (new-let [a 1
            b 2
            c 3]
           (+ a b c))

  (new-let [a 1
            b 2
            c 3]
           (+ 1 2 3))

  (new-let [a 1]
           (+ a 2)))

(defmacro like-ref-let [bindings & body]
  (let [[names items]
         (let [l (partition-all 2 bindings)]
           [(map first l) (map second l)])]
    `(fn [[~@names]] ~@body)))

(defmacro like-ref-let [bindings & body]
  (let [[names items]
         (let [l (partition-all 2 bindings)]
           [(map first l) (map second l)])]
    `((fn [[~@names]] ~@body) [~@items])))


(comment
  (like-ref-let [a 1
                 b 2
                 c 3]
                (+ a b c)))


