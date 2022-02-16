(ns code.check
  (:require-macros [code.macro :as c]))

(comment
  (c/new-let [a 1
            b 2
            c 3]
           (+ a b c))

  (c/new-let [a 1
            b 2
            c 3]
           (+ 1 2 3))

  (c/new-let [a 1]
           (+ a 2))

  (c/like-ref-let [a 1
                   b 2
                   c 3]
                  (+ a b c)))




