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
  ;;{:post [(c/item? %)]} ;; TODO
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

(defn runnerr
  [prog]
  (c/local-state
   prog
   (c/handle-action
    (c/dynamic
     (fn [[outer inner]]
       (dom/div
        (dom/samp (pr-str [outer inner]))
        (if (nil? inner)
          (c/fragment) ;; display nothing if done (inner = nil)
          (c/handle-action
           (c/focus
            first-lens ;; hide inner state (bookkeping) from item
            (show inner))
           (fn [[outer st] ac]
             (println (pr-str ac))
             (if (commit? ac)
               (if (bind? st)
                 (let [res ((bind-continuation st) (commit-payload ac))]
                   (cond
                     ;; if bind -> continue seq. comp
                     (or (bind? res) (prog? res)) (c/return :state [outer res])
                     (fn? res) (c/return
                                :action (fn [] (res outer))
                                :state [outer c/keep-state])
                     ;; result is non-bind -> emit (?) TODO
                     :else (c/return :action ac)
                     ))
                 ;; error handling:
                 ;; no continue but received commit
                 ;; emit value (?) TODO
                 (c/return :action ac))
               ;; propagate :action upwards
               (c/return :action ac))
             ))))))
    (fn [[outer inner] ac]
      (if (fn? ac)
        (let [res (ac)]
          (println (pr-str {:res res
                            :outer outer
                            :inner inner}))
          (c/merge-returned
           res
           (c/return :state [(c/returned-state res) nil])))
        (c/return :action ac))))))



(defn runner
  [prog]
  (c/local-state
   prog
   (c/dynamic
    (fn [[outer inner]]
      (dom/div
       (dom/samp (pr-str [outer inner]))
       (if (nil? inner)
        (c/fragment)
        (c/handle-action
         (c/focus
          first-lens
          (show inner))
         (fn [[outer inner] ac]
           (if (commit? ac)
             (if (bind? inner)
               (let [res ((bind-continuation inner) (commit-payload ac))]
                 (if (fn? res)
                   (let [res-res (res outer)]
                     (c/return :state [(c/returned-state res-res) nil]))
                   (c/return :state [outer ((bind-continuation inner) (commit-payload ac))])))
               (c/return :action ac))
             (c/return :action ac))))))))))

