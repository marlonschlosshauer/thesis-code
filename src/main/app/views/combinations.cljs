(ns app.views.combinations
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [app.views.util :as util]))

;; Helper function
(defn item [name]
  (dom/div
    (dom/h3 (str name))
    (util/click-me! (str name))))

(def left-hand
  ;; (Prog 1 >>= Prog 2) >>= Prog 3
  (b/make-bind
   ;; TODO: Not composible because Bind is not a Prog
   ;; and make-bind wants a Prog
   (b/make-bind (b/return (item "one")) (fn [_] (b/return (item "two"))))
   (fn [_] (item "three"))))

(def right-hand
  ;; Prog 1 >>= (Prog 2 >>= Prog 3)
  (b/make-bind
   (b/return (item "one"))
   (fn [_]
     (b/make-bind (b/return (item "two")) (fn [_] (item "three"))))))

(c/def-item main
  (dom/div
   (dom/h1 "combinations")
   (dom/div
    (dom/h3 "left-hand")
    ;; left-hand :: Bind Bind Prog Item a
    (b/runner left-hand)
    )
   (dom/div
    (dom/h3 "right-hand")
    ;; right-hand :: Bind Prog a
    (b/runner right-hand))))
