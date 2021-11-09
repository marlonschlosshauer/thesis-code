(ns code.scenes.change
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [code.scenes.util :as util]))

(defn get-position-in-queue []
  (js/setTimeout
   (fn []
     (let [pos (rand-int 100)]
       ;;(println pos)
       (c/return :action {:pos pos})))
   3000))

(c/defn-item next-button [cb]
  (dom/button {:onclick (fn [] (c/return :action (b/make-commit-action (cb))))} "Next"))

(c/defn-item greeting [greeting]
  (dom/div
   (dom/h2 "Greeting:" (pr-str greeting))
   (next-button get-position-in-queue)))

(c/defn-item waiting [pos]
  (c/local-state
   {:test 1}
   (dom/div
    (dom/h2 "Your position in queue is " (pr-str pos))
    (next-button (fn [] nil)))))

(c/defn-item goodbye []
  (dom/div
   (dom/h2 "Goodbye :)")))

(c/defn-item main []
  (util/wrap-state
   (b/bind
    (util/wrap-state (greeting {:greeting "nihao"}))
    (fn []
      (b/bind (util/wrap-state (waiting 12))
              (fn []
                (util/wrap-state (goodbye))))))))
