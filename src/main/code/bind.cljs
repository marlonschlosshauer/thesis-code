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

;; Bind :: Prog a -> (a -> Prog b) -> Bind
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
(defn runner [b]
  ;; TODO: Assert (item? prog)
  (c/isolate-state
   b
   (c/dynamic
    (fn [st]
      (c/handle-action
       (if (bind? st)
         ;; TODO: Focus on outter state
         (bind-item (show st))
         st)
       (fn [st ac]
         (if (and (commit? ac) (bind? st))
           ;; Save resulting bind/element in state
           (c/return :state ((:continuation st) (commit-payload ac))))))))))


(defn bind [prog f]
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
          (if (and (commit? ac) (not (:called st)))
            ;; TODO: Fix f call at end
            ;; Scenario: commit already responded to, how to bubble up value?
            ;; How do you know if you are done?
            (c/return :state (assoc st :prog (f (commit-payload ac)) :called true))))))))))
