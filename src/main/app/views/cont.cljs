(ns app.views.cont
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as u])
  (:require-macros [code.macros :as m]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def test-cont-then
  (b/runner
   (m/then [a (item 1)
            b (item 2)
            c (item 3)]
           (fn [x]
              (js/alert (pr-str [a b c x])) x))))

(def test-cont-runner
  (m/runner [a (item 1)
             b (item 2)
             c (item 3)]
            (fn [x]
              (js/alert (pr-str [a b c x])) x)))

(def main
  (dom/div
   (dom/div
    (dom/h2 "cont-then")
    test-cont-then)
   (dom/div
    (dom/h2 "cont-runner")
    test-cont-runner)))

