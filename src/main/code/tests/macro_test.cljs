(ns code.tests.macro-test
  ;;(:require-macros [code.macros.macroland :as ml])
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [code.tests.future :as fut]
            [code.macros.macroland :as m :include-macros true]))


(c/defn-item main []
  (dom/div "hello :D"))

(comment
  ;; Works!
  (ml/let2 [a 1
            b 2]
           (+ a b))
  ;; Does not work yet!
  (ml/let2 [a 1
            b (a + 1)]
           (+ a b)))

(comment
  (m/test-bind [personal fut/personal-info
                     code fut/create-and-send-verification-code
                     done fut/done]
                    (println personal)))
