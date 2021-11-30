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

(defrecord Bind [item continuation])
(defn make-bind
  [item continuation]
  (->Bind item continuation))

(defn bind? [x] (instance? Bind x))
(defn bind-continuation [b] (:continuation b))
(defn bind-item [b] (:item b))

(defrecord Prog [item])
(defn prog? [x] (instance? Prog x))
(defn make-prog [item] (->Prog item))

;; TODO: mondic return && monadic bind
;; Functionality of both is currently in "runner"
;; return: a -> Prog a
;; bind: Prog a -> (a -> Prog b) -> Prog b

;; Prog is an item that emits commit (eventually)

(defn return [item]
  (make-prog item))

(defn show [prog]
  (:item prog))

(defn runner [prog]
  ;; TODO: Assert (item? prog)
  (c/isolate-state
   prog
   (c/dynamic
    (fn [st]
      (c/handle-action
       ;; TODO: Focus on outter state
       (if (bind? st) (bind-item st) st)
       (fn [st ac]
         (if (and (commit? ac) (bind? st))
           ;; Save resulting bind/element in state
           (c/return :state ((:continuation st) (commit-payload ac))))))))))

(defn _bind [prog f]
  ;; show prog until commit happens then call f with result
  ;; TODO: break loop after initial commit
  (return
   (c/isolate-state
    prog
    (c/dynamic
     (fn [st]
       (c/handle-action
        (show st)
        (fn [st ac]
          (if (commit? ac)
            (c/return :state (f (commit-payload ac)))))))))))

(defn __bind [prog f]
  ;; show prog until commit happens then call f with result
  (return
   (c/handle-action
    (show prog)
    (fn [_ ac]
      (if (commit? ac) (c/return :state (f (commit-payload ac)))))))
  )

(defn bind [prog f id]
  ;; show prog until commit happens then call f with result
  ;; TODO: break loop after initial commit
  (return
   (c/isolate-state
    {:prog prog :called false}
    (c/dynamic
     (fn [st]
       (c/handle-action
        (show (:prog st))
        (fn [st ac]
          ;; If already called, let commit float upwards to other bind
          (println {:id id :ac ac})
          (if (and (commit? ac) (not (:called st)))
            ;; TODO: Fix f call at end
            ;; Scenario: commit already responded to, how to bubble up value?
            ;; How do you know if you are done?
            (c/return :state (assoc st :prog (f (commit-payload ac)) :called true))))))))))

