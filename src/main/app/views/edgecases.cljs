(ns app.views.edgecases
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.core :as b :include-macros true]
            [app.views.util :as u]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def scene0
  (dom/div
   (dom/p "((Prog 1 >>= (Prog 2 >>= Prog 3)) >>= Prog 4) >>= Prog 5")
   (b/-runner
    (b/-then
     (b/-then
      (b/-then
       (item "Prog 1")
       (fn [](b/then (item "Prog 2") (fn [] (item "Prog 3")))))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def scene1
  (dom/div
   (dom/p "Prog 1 >>= (((Prog 2 >>= Prog 3) >>= Prog 4) >>= Prog 5)")
   (b/-runner
    (b/-then
     (b/-then
      (b/-then
       (item "Prog 1")
       (fn []
         (b/-then (item "Prog 2")
                 (fn [] (item "Prog 3")))))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def scene2
  (dom/div
   (dom/p "(((Prog 1 >>= Prog 2) >>= Prog 3) >>= Prog 4) >>= Prog 5")
   (b/-runner
    (b/-then
     (b/-then
      (b/-then
       (b/-then
        (item "Prog 1")
        (fn [] (item "Prog 2")))
       (fn [] (item "Prog 3")))
      (fn [] (item "Prog 4")))
     (fn [] (item "Prog 5"))))))

(def scene3
  (dom/div
   (dom/p "Prog 1 >>= (Prog 2 >>= Prog 3)")
   (b/-runner
    (b/-then
     (item "Prog 1")
     (fn []
       (b/-then
        (item "Prog 2")
        (fn [] (item "Prog 3"))))))))

(def scene4
  (dom/div
   (dom/p "Prog 1")
   (b/-runner
    (item "Prog 1"))))

(def scene5
  (dom/div
   (dom/p "Prog 1 >>= Prog 2 >>= Prog 3 >>= Prog 4 >>= Prog 5")
   (b/-runner
    (b/-then
     (item "Prog 1")
     (fn [] (b/-then
             (item "Prog 2")
             (fn [] (b/-then
                     (item "Prog 3")
                     (fn [] (b/-then (item "Prog 4")
                                    (fn [] (item "Prog 5"))))))))))))

(def scene6
  (dom/div
   (dom/p "Prog 1 >>= (Prog 2 >>= Prog 3) >>= (Prog 4 >>= Prog 5)")
   (let [p1 (b/-then (item "Prog 2")
                    (fn [] (item "Prog 3")))
         p2 (b/-then (item "Prog 4")
                    (fn [] (item "Prog 5")))]
     (b/-runner
      (b/-then
       (item "Prog 1")
       (fn []
         (b/-then p1
                 (fn [] p2))))))))

(def main
  (dom/div
   scene0
   scene1
   scene2
   scene3
   scene4
   scene5
   scene6))

