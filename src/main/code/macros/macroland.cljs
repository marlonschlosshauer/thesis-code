(ns code.macros.macroland
  (:require-macros [code.macros.macroland :as ml])
  (:require [code.bind :as b]))

(comment
  (defn bind [item handler]
    (ml/make-bind b/bind) item handler))

(comment
 (defn bind [item handler]
    "blub"))

