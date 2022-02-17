(ns app.home
  (:require [reacl-c.main :as main]
            [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom]
            [app.views.stress :as stress]
            [app.views.registration :as registration]
            [app.views.combinations :as combinations]
            [app.views.edgecases :as edge]
            [app.views.macros :as macros]
            [app.views.cont :as cont]))

(c/def-item display-views
  (dom/div {:style {:display "grid" :gridTemplateColumns "repeat(3,1fr)"}}
   (dom/div
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "future")
             registration/main)
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "stress")
             (stress/main 0)))
   (dom/div
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "combination")
             combinations/main)
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "edgecases")
             edge/main))
   (dom/div
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "macros")
             macros/main)
    (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
             (dom/h1 "continuation")
             cont/main)
    )))


(defn init []
  (main/run (.getElementById js/document "app")
    display-views
    {:initial-state {:dressing "salad"}}))

