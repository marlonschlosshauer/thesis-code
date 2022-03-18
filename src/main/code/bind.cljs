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

(comment
  (defn runner
  [prog]
  (c/local-state
   prog
   (c/dynamic
    (fn [[outter inner]]
      (cond
        (nil? inner)
        (c/fragment)
        (fn? inner)
        (c/focus
         first-lens
         (c/fragment
          (c/once (fn [] (inner [outter])))
          (c/finalize (fn [] (c/return :state [[outter nil]])))))
        :else
        (c/handle-action
         (c/focus
          first-lens
          (show inner))
         (fn [[outter st] ac]
           (if (and (commit? ac) (bind? st))
             (c/return :state [outter ((bind-continuation st) (commit-payload ac))])
             (c/return :action ac))))))))))


(comment
  (defn runner
  [prog]
  (c/local-state
   prog
   (c/dynamic
    (fn [[outter inner]]
      (dom/div
       (dom/samp (pr-str [outter inner]))
       (if (nil? inner)
         (c/fragment)
         (c/handle-action
          (c/focus
           first-lens
           (show inner))
          (fn [[outter st] ac]
            ;; Trying "done" type
            (if (and (commit? ac) (bind? st))
              (let [res ((bind-continuation st) (commit-payload ac))]
                (c/return :state (if (bind? res) ;; for some reason can't check with map?
                                   [outter res]
                                   [((:end-expr res) outter) nil]
                                   ;;[outter nil] ;; doesn't run twice
                                   ;;[nil nil] ;;  does run twice
                                   )))
              (c/return :action ac)))))))))))




;; state change of outter causes re-draw of runner, which then starts at step 0 again

(comment
  (defn runner
    [prog]
    (c/local-state
     prog
     (c/dynamic
      (fn [[outter inner]]
        (println (pr-str inner))
        (if (nil? inner)
          (c/fragment)
          (c/handle-action
           (c/focus
            first-lens
            (show inner))
           (fn [[outter st] ac]
             ;; Trying "done" type
             (if (and (commit? ac) (bind? st))
               (let [res ((bind-continuation st) (commit-payload ac))]
                 (c/return :state (if (bind? res) ;; for some reason can't check with map?
                                    [outter res]
                                    ;;[((:end-expr res) outter) nil]
                                    [outter nil] ;; doesn't run twice
                                    ;;[nil nil] ;;  does run twice
                                    )))
               (c/return :action ac))))))))))


(comment
  (c/return :state
            (if (and
                 (fn? (bind-continuation st))
                 (map? ((bind-continuation st) [nil nil])))
              [((:end-expr (bind-continuation st)) (commit-payload ac)) nil]
              [outter ((bind-continuation st) (commit-payload ac))])))


(comment
  (defn runner
  [prog]
  (c/local-state
   prog
   (c/dynamic
    (fn [[outter inner]]
      (if (nil? inner)
        (c/fragment)
        (c/handle-action
         (c/focus
          first-lens
          (show inner))
         (fn [[outter st] ac]
           (if (commit? ac)
             (cond
               (bind? st) (c/return :state [outter ((bind-continuation st) (commit-payload ac))])
               (fn? st) (st outter)
               :else (c/return :action ac))
             (c/return :action ac)))))))))

)


(defn runner
    [prog]
    (c/local-state
     prog
     (c/dynamic
      (fn [[outter inner]]
        (println (pr-str inner))
        (dom/div
         (c/fragment
          ;; (c/once (fn []
          ;;           (println "moin!")
          ;;           (c/return)))
          (c/cleanup (fn []
                       (println "goodbye!")
                       (c/return))))
         (dom/samp (pr-str [outter inner]))
         (if (nil? inner)
           (c/fragment)
           (c/handle-action
            (c/focus
             first-lens
             (show inner))
            (fn [[outter st] ac]
              ;; Trying "done" type
              (if (and (commit? ac) (bind? st))
                (let [res ((bind-continuation st) (commit-payload ac))]
                  (c/return :state (if (bind? res) ;; for some reason can't check with map?
                                     [outter res]
                                     ;;[((:end-expr res) outter) nil]
                                     ;;[outter nil] ;; doesn't run twice
                                     [nil nil] ;;  does run twice
                                     )))
                (c/return :action ac))))))))))


;;




;; The problem is that inner needs to be set to nil after end-expr is executed (which probably is :return)
;; Obvious solution would be to pass end-expr to the runner. This however does not work with the current macros. end-expr might want to access results of prev. steps. To have access to them it needs to come from a then. Otherwise it's not path of the nested then calls.

;; Possible impl. if end-expr argument:
(comment
  (defn runner
    [prog end-expr]
    (c/local-state
     prog
     (c/dynamic
      (fn [[outter inner]]
        (if (nil? inner)
          (end-expr outter)
          (c/handle-action
           (c/focus
            first-lens
            (show inner))
           (fn [[outter st] ac]
             (if (commit? ac)
               (c/return
                :state
                [outter
                 (if (bind? st)
                   ((bind-continuation st)
                    (commit-payload ac))
                   nil)])
               (c/return :action ac))))))))))

;; Cannot check if inner == value: What if intermediate is just result




(comment
;; GOOD WORKING BASE!
  (defn runner
  [prog]
  (c/local-state
   prog
   (c/dynamic
    (fn [[outter inner]]
      (dom/div
       (dom/samp (pr-str [outter inner]))
       (if (nil? inner)
        (c/fragment)
        (c/handle-action
         (c/focus
          first-lens
          (show inner))
         (fn [[outter st] ac]
           (if (commit? ac)
             (cond
               (bind? st) (c/return :state [outter ((bind-continuation st) (commit-payload ac))])
               (fn? st) (st outter)
               :else (c/return :action ac))
             (c/return :action ac)))))))))))
