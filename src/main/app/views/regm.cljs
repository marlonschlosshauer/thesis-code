(ns app.views.regm
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.core :as b :include-macros true]
            [app.views.registration :refer [personal-info create-and-send-verification-code done]])
  )

(def main
  (b/runner [personal personal-info
             code create-and-send-verification-code
             d (done [personal code])]))
