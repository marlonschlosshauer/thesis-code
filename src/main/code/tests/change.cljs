(ns code.tests.change
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]))

(defn get-position-in-queue []
  (js/setTimeout
   #(c/return :action {:pos (rand-int 100)})
   3000))

(defn next-button []
  (dom/button {:onclick (fn [] (c/return :action (b/make-commit-action)))} "Next"))

(defn greeting [greeting]
  (dom/div
   (dom/h2 "Greeting:" (pr-str greeting))
   (next-button)))

(defn waiting [pos]
  (dom/div
   (dom/h2 "Your position in queue is " (pr-str pos))
   (next-button)))

(defn goodbye []
  (dom/div
   (dom/h2 "Goodbye :)")))

(defn main []
  (c/local-state
   {:greeting "nihao"}
   (b/bind
    (greeting {:greeting "nihao"})
    (fn []
      (b/bind (c/empty get-position-in-queue)
               (fn []
                 (b/bind (waiting 12)
                          (fn []
                            (goodbye)))))))))
