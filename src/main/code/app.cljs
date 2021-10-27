(ns code.app
  (:require [reacl-c.main :as main]
            [code.tests.current :as current]
            [code.tests.future :as future]
            [code.tests.change :as change]
            [code.tests.stress :as stress]))

(defn init []
  (main/run (.getElementById js/document "app")
  (stress/main-but-no-tra 0)
  {:initial-state {}}))

