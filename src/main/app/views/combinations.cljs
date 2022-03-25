(ns app.views.combinations
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.core :as b :include-macros true]
            [app.views.util :as util]))

;; Helper function
(defn item [name]
  (dom/div
   (dom/h3 (str name))
   (util/click-me! (str name))))

(def left-hand
  ;; (Prog 1 >>= Prog 2) >>= Prog 3
  (b/-then
   (b/-then
    (b/return (item "one"))
    (fn [_] (b/return (item "two"))))
   (fn [_] (item "three"))))

(def right-hand
  ;; Prog 1 >>= (Prog 2 >>= Prog 3)
  (b/-then
   (b/return (item "one"))
   (fn [_]
     (b/-then (b/return (item "two")) (fn [_] (item "three"))))))

(c/def-item main
  (dom/div
   (dom/div
    (dom/h3 "left-hand")
    (b/-runner left-hand))
   (dom/div
    (dom/h3 "right-hand")
    (b/-runner right-hand))))
