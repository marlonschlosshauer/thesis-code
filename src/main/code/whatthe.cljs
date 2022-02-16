(ns code.whatthe
  (:require-macros [code.macros :as m]))

(m/like-ref-let [a 1
                 b 2
                 c 3]
                (+ a b c))
