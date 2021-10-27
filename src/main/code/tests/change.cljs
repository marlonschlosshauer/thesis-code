(ns code.tests.change
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]))

(defn get-position-in-queue []
  (js/setTimeout
   (fn []
     (let [pos (rand-int 100)]
       (println pos)
       (c/return :action {:pos pos})))
   3000))

(c/defn-item next-button [cb]
  (dom/button {:onclick (fn [] (c/return :action (b/make-commit-action (cb))))} "Next"))

(c/defn-item greeting [greeting]
  (dom/div
   (dom/h2 "Greeting:" (pr-str greeting))
   (next-button get-position-in-queue)))

(c/defn-item waiting [pos]
  (dom/div
   (dom/h2 "Your position in queue is " (pr-str pos))
   (next-button (fn [] ()))))

(c/defn-item goodbye []
  (dom/div
   (dom/h2 "Goodbye :)")))

(c/defn-item main []
  (c/local-state
   {:greeting "nihao"}
   (b/bind
    (greeting {:greeting "nihao"})
    (fn []
      (b/bind (waiting 12)
              (fn []
                (goodbye)))))))
