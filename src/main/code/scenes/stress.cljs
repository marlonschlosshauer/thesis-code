(ns code.scenes.stress
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [code.scenes.util :as util]
            [code.next :as next]))

(c/defn-item stress [val]
  (c/local-state
   {:now (.now js/Date)}
   (c/dynamic
    (fn [[outter inner]]
      (dom/div
       (dom/h2 (pr-str {:val val :now (:now inner) :outter outter}))
       (dom/button {:onclick (fn [state action] (c/return :action (next/make-commit (+ val 1)))) :id "continue"} "click me"))))))

(defn evil-stress [val]
  (next/make-bind (stress val) evil-stress))

(defn evil-main [val]
  (next/evil-runner (evil-stress val)))

;; const b = setInterval(() => document.getElementById('continue').click(), 5);
