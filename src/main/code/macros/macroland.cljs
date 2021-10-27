(ns code.macros.macroland
  (:require-macros [code.macros.macroland :as ml]))


;; (ml/infox (1 + 1))

;; (ml/let2
;;  [a 1
;;   b 2
;;   c 3
;;   d 4]
;;  (+ a b))

;; (defn ffff [bindings & body]
;;   body)

;; (ffff  +)

;; (let [l ["a" 1
;;          "b" 2]]
;;   {:first (take-nth 2 l)
;;    :second (take-nth 2 (next l))})

;; ((fn [a b]
;;    (+ a b)) 1 2)

;; (fn [i c]
;;   (bind i c) )

;; (bind p
;;       (fn [p]
;;         (bind c
;;               (fn [c]
;;                 n))))

;; (let [a (range 5)]
;;   ((fn [[x y & rest]]
;;     rest) a))

;; (take 2 ['a' 1 'b' 2 'c' 3])
