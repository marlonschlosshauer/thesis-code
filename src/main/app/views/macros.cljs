(ns app.views.macros
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [app.views.util :as u]
            [code.bind :as b])
  (:require-macros [code.macros :as m]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def test-macro-then
  (b/runner
   (m/then [a (item 1)
            b (item 2)
            c (item 3)]
           (fn [x] (println x) x))))

(def test-macro-runner
  (m/runner [a (item 1)
             b (item (+ a 1))
             c (item (+ a b))]
            (fn [x] (println x) x)))

(def main
  (dom/div
   (dom/div
    (dom/h2 "test-macro-then")
    test-macro-then)
   (dom/div
    (dom/h2 "test-macro-runner")
    test-macro-runner)))

