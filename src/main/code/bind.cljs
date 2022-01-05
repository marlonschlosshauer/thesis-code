(ns code.bind
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]))

(defrecord Commit [payload])
(defn make-commit
  "A commit is data that will be emmitted by a Prog to continue the flow"
  ([]
   (map->Commit {}))
  ([payload]
   (->Commit payload)))

(defn commit? [x] (instance? Commit x))
(defn commit-payload [commit] (:payload commit))

;; Bind :: Prog a -> (a -> Prog b) -> Bind b
(defrecord Bind [prog continuation called])
;; TODO: auto wrap item with Prog
(defn make-bind
  [prog continuation]
  (->Bind prog continuation false))

(defn bind? [x] (instance? Bind x))
(defn bind-continuation [b] (:continuation b))
(defn bind-item [b] (:prog b))
(defn bind-called [b] (:called b))

(defrecord Prog [item])
(defn prog? [x] (instance? Prog x))
(defn prog-item [x] (:item x))
(defn make-prog [item] (->Prog item))

(defn thena [item cont]
  (make-bind (if (c/item? item) (make-prog item) item) cont))

;; CPS transformation
;; on bind: check if left hand is already a bind, re-write to make consumption easier!
(defn then [prog cont]
  {:pre [(or (bind? prog) (prog? prog) (c/item? prog))]
   :post [(bind? %)]}
  (if (bind? prog)
    (make-bind (bind-item prog) (fn [x] (then ((bind-continuation prog) x) cont)))
    (make-bind (if (c/item? prog) (make-prog prog) prog) cont)))

(defn bind-or-prog? [x]
  (or (prog? x) (bind? x)))

(defn return
  "Signify that this Item is a Prog, therefore emitting a commit at some point"
  [item]
  (make-prog item))

(defn show
  "Display Item inside of Prog"
  [x]
  (cond
    (prog? x) (prog-item x)
    (bind? x) (prog-item (bind-item x))
    (c/item? x) x))

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
         (bind? st) (show (bind-item st))
         (prog? st) (show st) ;; todo
         :else st)
       (fn [st ac]
         (if (and (commit? ac) (bind? st))
           ;; Save resulting bind/element in state
           ;; TODO: Fix f call at end
           ;; Scenario: commit already responded to, how to bubble up value?
           ;; How do you know if you are done?
           (c/return :state ((bind-continuation st) (commit-payload ac))))))))))
