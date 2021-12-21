(ns app.views.stress
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [app.views.util :as util]
            [code.bind :as bind]))

(c/defn-item stress [val]
  (c/local-state
   {:now (.now js/Date)}
   (c/dynamic
    (fn [[outter inner]]
      (dom/div
       (dom/h2 (pr-str {:val val :now (:now inner) :outter outter}))
       (dom/button {:onclick (fn [state action] (c/return :action (bind/make-commit (+ val 1)))) :id "continue"} "click me"))))))

(defn bundle-stress [val]
  (bind/make-bind (bind/make-prog (stress val)) bundle-stress))

(defn main [val]
  (bind/runner (bundle-stress val)))

;; const b = setInterval(() => document.getElementById('continue').click(), 5);
