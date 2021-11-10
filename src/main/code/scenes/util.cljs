(ns code.scenes.util
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]))

(defn wrap-state [item]
  (c/dynamic
   (fn [state]
     (dom/div
      (dom/samp (pr-str state))
      item))))

(defn logger [msg cont]
  (println msg)
  cont)
