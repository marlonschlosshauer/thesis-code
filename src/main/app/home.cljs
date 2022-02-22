(ns app.home
  (:require [reacl-c.main :as main]
            [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom]
            [app.views.stress :as stress]
            [app.views.registration :as registration]
            [app.views.combinations :as combinations]
            [app.views.edgecases :as edge]
            [app.views.macros :as macros]
            [app.views.cont :as cont]
            [app.views.regm :as regm]
            [app.views.testm :as testm]))

(def border-style
  {:border "1px solid black" :padding "15px" :margin "5px"})

(c/def-item display-primitives
  (dom/div
   {:style border-style}
   (dom/h1 "primitives")
   (dom/div
    {:style {:display "grid" :gridTemplateColumns "repeat(3,1fr)" :gridTemplateRows "repeat(2,1fr)"}}
    (dom/div {:style (merge border-style {:gridRow "1" :gridColumn "2"})}
             (dom/h1 "registration")
             registration/main)
    (dom/div {:style (merge border-style {:gridRow "2" :gridColumn "2"})}
             (dom/h1 "stress")
             (stress/main 0))
    (dom/div {:style (merge border-style {:gridRow "1" :gridColumn "3"})}
             (dom/h1 "combination")
             combinations/main)
    (dom/div {:style (merge border-style {:gridRow "1/3" :gridColumn "1"})}
             (dom/h1 "edgecases")
             edge/main))))

(c/def-item display-macros
  (dom/div
   {:style border-style}
   (dom/h1 "macros")
   (dom/div
    {:style {:display "grid" :gridTemplateColumns "repeat(3,1fr)"}}
    (dom/div {:style border-style}
             (dom/h1 "macros")
             macros/main)
    (dom/div {:style border-style}
             (dom/h1 "continuation")
             cont/main)
    (dom/div {:style border-style}
             (dom/h1 "registration-macros")
             regm/main)
    (dom/div {:style border-style}
             (dom/h1 "registration-macros")
             testm/main))))

(c/def-item display-views
  (dom/div
   display-primitives
   display-macros))

(defn init []
  (main/run (.getElementById js/document "app")
    display-views
    {:initial-state {:dressing 1}}))


