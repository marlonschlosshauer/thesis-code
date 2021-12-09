(ns code.util
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as bind]))

(defn wrap-as-prog [item]
  (c/handle-action
   item
   (fn [st ac]
     (c/return :action (if-not (bind/commit? ac) (bind/make-commit ac) ac)))))


