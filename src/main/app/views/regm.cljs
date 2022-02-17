(ns app.views.regm
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.registration :refer [personal-info create-and-send-verification-code done]])
  (:require-macros [code.macros :as m]))

(def main
  (m/runner [personal personal-info
             code create-and-send-verification-code
             d (done [personal code])]))
