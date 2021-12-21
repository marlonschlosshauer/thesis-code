(ns code.bind
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]))

(defrecord Commit [payload])
(defn make-commit
  ([]
   (map->Commit {}))
  ([payload]
   (->Commit payload)))

(defn commit? [x] (instance? Commit x))
(defn commit-payload [commit] (:payload commit))

;; data Prog a :: P Item a | B Bind a
;; Bind :: Prog a -> (a -> Prog b) -> Bind b
(defrecord Bind [prog continuation])
(defn make-bind
  [prog continuation]
  (->Bind prog continuation))

(defn bind? [x] (instance? Bind x))
(defn bind-continuation [b] (:continuation b))
(defn bind-item [b] (:prog b))

(defrecord Prog [item])
(defn prog? [x] (instance? Prog x))
(defn make-prog [item] (->Prog item))

(defn return [item]
  (make-prog item))

(defn show [prog]
  (:item prog))

;; runner :: Bind -> Item
(defn runner
  "Show Prog in received Bind until commit happens, then call f from Bind with result"
  [b]
  {:pre [(bind? b)]}
  (c/isolate-state
   b
   (c/dynamic
    (fn [st]
      (c/handle-action
       (cond
         (bind? st) (show (bind-item st)) ;; TODO: Flatmap?
         (prog? st) (show st) ;; todo
         (c/item? st) st)
       (fn [st ac]
         (if (and (commit? ac) (bind? st))
           ;; Save resulting bind/element in state
           ;; TODO: Fix f call at end
           ;; Scenario: commit already responded to, how to bubble up value?
           ;; How do you know if you are done?
           (c/return :state ((:continuation st) (commit-payload ac))))))))))
