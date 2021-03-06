(ns app.views.util
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.core :as b :include-macros true]))

(defn wrap-state [item]
  (c/dynamic
   (fn [state]
     (dom/div
      (dom/samp (pr-str state))
      item))))

(defn logger [msg cont]
  (println msg)
  cont)

(defn click-me!
  ([]
   (click-me! {}))
  ([args]
   (dom/button {:onclick (fn [_ _] (c/return :action (b/make-commit args)))} "click-me!")))

(defn named-click-me! [name]
  (dom/div
    (dom/h3 (str name))
    (click-me! name)))
