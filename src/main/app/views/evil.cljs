(ns app.views.evil
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as u]
            [code.util])
  (:require-macros [code.wow :as m]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def scene2
  (dom/div
   (b/runner
    (b/then
     (b/then
      (b/then
       (b/then
        (item 1)
        (fn [x] (item (inc x))))
       (fn [y] (item (inc y))))
      (fn [z] (item (inc z))))
     (fn [v] (item (inc v)))))))

(def BOOM
  (b/runner
   (m/fatmamba [a (item 1)
                b (item 2)
                c (item 3)]
               (fn [x] (println x) x))))

(def main
  (dom/div
   BOOM))

