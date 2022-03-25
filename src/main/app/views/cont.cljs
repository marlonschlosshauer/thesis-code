(ns app.views.cont
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.core :as b :include-macros true]
            [app.views.util :as u]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def test-then-cont
  (b/-runner
   (b/then [a (item 12)
            b (item (+ a 123))
            c (item (+ b (- a 25.03)))]
           (fn []
             (println (pr-str [a b c]))))))

(def test-runner-cont
  (b/runner [a (item 12)
             b (item (+ a 123))
             c (item (+ b (- a 25.03)))]
            (fn []
              (println (pr-str [a b c])))))

(def test-runner-no-cont
  (b/runner [a (item 12)
             b (item (+ a 123))
             c (item (+ b (- a 25.03)))]))

(def test-runner-cont-return
  (b/runner [a (item 12)
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


