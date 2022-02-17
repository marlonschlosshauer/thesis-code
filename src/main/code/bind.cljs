(ns code.bind
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]))

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

(defrecord Bind [prog continuation called])
(defn make-bind
  "Takes a `Prog` and a `continuation` of type (a -> Prog b) and returns a Bind"
  [prog continuation]
  {:pre [(prog? prog)]}
  (->Bind prog continuation false))

(defn bind? [x] (instance? Bind x))
(defn bind-continuation [b] (:continuation b))
(defn bind-item [b] (:prog b))
(defn bind-called [b] (:called b))

(defn then
  "Bind a `Prog` to a continuation. Returns a `Bind`. cont should return a `Bind` or `Prog`"
  [prog cont]
  {:pre [(and (or (bind? prog) (prog? prog) (c/item? prog))
              (fn? cont))]
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
  {;;:pre [(or (bind? x) (prog? x) (c/item? x))]
   :post [(c/item? %)]}
  (cond
    (prog? x) (prog-item x)
    (bind? x) (prog-item (bind-item x))
    (c/item? x) x
    :else (c/fragment)))

(defn runner
  "Show `Prog` (or `Bind`). Returns an `Item`"
  [b]
  {:pre [(or (bind? b) (prog? b))]}
  ;; inner state of book-keeping is the bind
  (c/isolate-state
   b
   (c/dynamic
    (fn [st]
      (c/handle-action
         ;; display item in bind/prog
         (show st) ;; TODO: lens
         (fn [st ac]
           ;; call continuation of bind on commit
           ;; TODO: what if it's just prog?
           (if (and (commit? ac) (bind? st))
             (c/return :state ((bind-continuation st) (commit-payload ac))))))))))
