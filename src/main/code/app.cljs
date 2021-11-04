(ns code.app
  (:require [reacl-c.main :as main]
            [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom]
            [code.tests.current :as current]
            [code.tests.future :as future]
            [code.tests.change :as change]
            [code.tests.stress :as stress]
            [code.tests.macro-test :as mtest]))


(comment
  (c/def-item display-tests
    (dom/div
     (map (fn [item]
            (dom/div {:style {:border "1px solid black" :padding "15px"}}
                     (dom/h1 (:name item ))
                     (:item item)))
          [{:name "change" :item (change/main)}
           {:name "future" :item future/main}
           {:name "stress" :item (stress/main 0)}]))))

(c/def-item display-tests
  (dom/div
   (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
            (dom/h1 "change")
            (change/main))
   (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
            (dom/h1 "future")
            future/main)
   (dom/div {:style {:border "1px solid black" :padding "15px" :margin "5px"}}
            (dom/h1 "stress")
            (stress/main 0))))

(defn init []
  (main/run (.getElementById js/document "app")
    ;;(change/main)
    ;;future/main
    display-tests
    {:initial-state {:dressing "salad"}}))

