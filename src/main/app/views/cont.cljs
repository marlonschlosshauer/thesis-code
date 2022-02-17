(ns app.views.cont
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as u])
  (:require-macros [code.macros :as m]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def test-then-cont
  (b/runner
   (m/then [a (item 12)
            b (item (+ a 123))
            c (item (+ b (- a 25.03)))]
           (fn []
             (println (pr-str [a b c]))))))

(def test-runner-cont
  (m/runner [a (item 12)
             b (item (+ a 123))
             c (item (+ b (- a 25.03)))]
            (fn []
              (println (pr-str [a b c])))))

(def test-runner-no-cont
  (m/runner [a (item 12)
             b (item (+ a 123))
             c (item (+ b (- a 25.03)))]))

(def test-runner-cont-return
  (m/runner [a (item 12)
             b (item (+ a 123))
             c (item (+ b (- a 25.03)))]
            (fn [] [a b c])))

(def main
  (dom/div
   (dom/div
    (dom/h2 "test-then-cont")
    test-then-cont)
   (dom/div
    (dom/h2 "test-runner-cont")
    test-runner-cont)
   (dom/div
    (dom/h2 "test-runner-no-cont")
    test-runner-no-cont)
   (dom/div
    (dom/h2 "test-runner-cont-return")
    test-runner-cont-return)))


