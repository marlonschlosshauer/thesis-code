(ns code.macros.macroland)
  ;;(:require [code.bind :as b]))
  ;;(:require [code.macros.macroland :as ml])

(defmacro let2 [bindings & body]
  `((fn [~@(take-nth 2 bindings)]
      ~@body)
    ~@(take-nth 2 (next bindings))))

(defn blub [item handler]
  "blubbed")

;; this works!
(defmacro test-bind [bindings & body]
  `((fn [~@(take-nth 2 bindings)]
      ~@body)
    ~@(map (fn [x] (blub x (fn [y] y)))
           (take-nth 2 (next bindings)))))

;; this does not work
(comment
  (defmacro make-bind [f]
    (fn [bindings & body]
      `((fn [~@(take-nth 2 bindings)]
          ~@body)
        ~@(map (fn [x] (f x (fn [y] y)))
               (take-nth 2 (next bindings)))))))

(comment
  (+ 1 2)
  ;; => 3

  (quote (+ 1 2))
  ;; => (+ 1 2)

  (quote (+ 1 (+ 1 1)))
  ;; => (+ 1 (+ 1 1))

  (quote (+ 1 ~(+ 1 1)))
  ;; => (+ 1 2)
  )
