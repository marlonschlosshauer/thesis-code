(ns code.bind
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom])
  ;;(:require-macros [code.bind])
  )

(defrecord Commit [payload])
(defn make-commit
  "A `commit` is data that will be emmitted by a `Prog` to continue the sequence"
  ([]
   (map->Commit {}))
  ([payload]
   (->Commit payload)))

(defn commit? [x] (instance? Commit x))
(defn commit-payload [commit] (:payload commit))

(defrecord Prog [item])
(defn prog? [x] (instance? Prog x))
(defn prog-item [x] (:item x))
(defn make-prog
  [item]
  {:pre [(c/item? item)]}
  (->Prog item))

(defrecord Bind [prog continuation])
(defn make-bind
  "Takes a `Prog` and a `continuation` of type (a -> Prog b) and returns a Bind"
  [prog continuation]
  {:pre [(prog? prog)]}
  (->Bind prog continuation))

(defn bind? [x] (instance? Bind x))
(defn bind-continuation [b] (:continuation b))
(defn bind-item [b] (:prog b))

(defn then
  "Bind a `Prog` to a continuation. Returns a `Bind`. cont should return a `Bind` or `Prog`"
  [prog cont]
  {:pre [(or (bind? prog) (prog? prog) (c/item? prog))
         (fn? cont)]
   :post [(bind? %)]}
  (if (bind? prog)
    ;; if left hand is already a bind, swap (csp transformation)
    ;; bind with item of bind on left hand to not cause nested binds!
    (make-bind (bind-item prog) (fn [x] (then ((bind-continuation prog) x) cont)))
    (make-bind (if (c/item? prog) (make-prog prog) prog) cont)))

(defn return
  "Signify that this `Item` is a `Prog`, emitting a `commit` at some point"
  [item]
  {:pre [(c/item? item)]}
  (make-prog item))

(defn show
  "Display `Item` inside of `Prog` (or `Bind`)"
  [x]
  {:post [(c/item? %)]}
  (cond
    (prog? x) (prog-item x)
    (bind? x) (prog-item (bind-item x))
    (c/item? x) x
    :else (c/fragment)))

(defn first-lens
  ([[first & _]]
   first)
  ([[_ & rest] small]
   (vec (cons small rest))))

(defn runner
  "Show `Prog` (or `Bind`). Returns an `Item`"
  [b]
  {:pre [(or (bind? b) (prog? b))]}
  ;; inner state of book-keeping is the bind
  (c/local-state
   b
   (c/dynamic
    (fn [[outter st]]
      (c/handle-action
       ;; display item in bind/prog
       (c/focus
        first-lens
        (show st))
       (fn [[outter st] ac]
         ;; call continuation of bind on commit
         (if (and (commit? ac) (bind? st))
           (c/return :state [outter ((bind-continuation st) (commit-payload ac))]))))))))
