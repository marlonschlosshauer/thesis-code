(ns code.test
  (:require [code.util])
  (:require-macros [code.wow :as m]))

(m/new-let [a 1
            b 2
            c 3]
           (+ a b c))


