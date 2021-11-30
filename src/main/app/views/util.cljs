(ns app.views.util
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]))

(defn wrap-state [item]
  (c/dynamic
   (fn [state]
     (dom/div
      (dom/samp (pr-str state))
      item))))

(defn logger [msg cont]
  (println msg)
  cont)
