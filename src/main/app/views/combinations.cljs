(ns app.views.combinations
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as util]))

;; Helper function
(defn item [name]
  (b/return
   (dom/div
    (dom/h3 (str name))
    (util/click-me! (str name)))))

(def left-hand
  ;; (Prog 1 >>= Prog 2) >>= Prog 3
  (b/bind
   (b/bind (item "one") (fn [_] (item "two")))
   (fn [_] (item "three"))))

(def right-hand
  ;; Prog 1 >>= (Prog 2 >>= Prog 3)
  (b/bind (item "one")
          (fn [_]
            (b/bind (item "two") (fn [_] (item "three"))))))

(c/def-item main
  (dom/div
   (dom/h1 "combinations")
   (dom/div
    (dom/h3 "left-hand")
    (b/show left-hand))
   (dom/div
    (dom/h3 "right-hand")
    (b/show right-hand))))
