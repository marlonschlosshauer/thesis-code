(ns code.tests.stress
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]))

(c/defn-item stress [val]
  (c/local-state
   {:now (.now js/Date)}
   (c/dynamic
    (fn [[outter inner]]
      (dom/div
       (dom/h2 (pr-str {:val val :now (:now inner)  :outter outter}))
       (dom/button {:onclick (fn [state action] (c/return :action (b/make-commit-action (+ val 1)))) :id "continue"} "click me"))))))

(defn strss [val]
  (b/bind (stress val)
            strss))

(defn main [val]
  ((b/tra strss) val))

(defn main-but-no-tra [val]
  (strss val))

;; const b = setInterval(() => document.getElementById('continue').click(), 5);
