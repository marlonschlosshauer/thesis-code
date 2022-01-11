(ns app.views.edgecases
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as u]))

(defn item [name]
  (b/return (u/named-click-me! name)))

;; ((Prog 1 >>= (Prog 2 >>= Prog 3)) >>= Prog 4) >>= Prog 5
;; Prog 1 >>= (((Prog 2 >>= Prog 3) >>= Prog 4) >>= Prog 5)
;; (((Prog 1 >>= Prog 2) >>= Prog 3) >>= Prog 4) >>= Prog 5

(def scene0
  (dom/div
   (dom/p "((Prog 1 >>= (Prog 2 >>= Prog 3)) >>= Prog 4) >>= Prog 5")
   (b/runner
    (b/then
     (b/then
      (b/then
       (item "Prog 1")
       (fn [](b/then (item "Prog 2") (fn [] (item "Prog 3")))))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def scene1
  (dom/div
   (dom/p "Prog 1 >>= (((Prog 2 >>= Prog 3) >>= Prog 4) >>= Prog 5)")
   (b/runner
    (b/then
     (b/then
      (b/then
       (item "Prog 1")
       (fn []
         (b/then (item "Prog 2")
                 (fn [] (item "Prog 3")))))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def scene2
  (dom/div
   (dom/p "(((Prog 1 >>= Prog 2) >>= Prog 3) >>= Prog 4) >>= Prog 5")
   (b/runner
    (b/then
     (b/then
      (b/then
       (b/then
        (item "Prog 1")
        (fn [] (item "Prog 2")))
       (fn [] (item "Prog 3")))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def main
  (dom/div
   scene0
   scene1
   scene2))

