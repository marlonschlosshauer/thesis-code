(ns code.macros.macroland)

;; (defmacro infox
;;   [i]
;;   (list (second i) (first i) (last i)))


;; (defmacro let2 [bindings & body]
;;   `((fn [~@(take-nth 2 bindings)]
;;       ~@body)
;;     ~@(take-nth 2 (next bindings))))

;; (defn b [i c]
;;   i)

;; (defmacro bb [bindings]
;;   `((fn [~@(first bindings)]
;;       b)))

;; (let [args ['a' 1 'b' 2 'c' 3]]
;;   (bb args))
