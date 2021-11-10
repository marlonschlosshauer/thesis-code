(ns code.next
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

(defn runner [prog]
  ;; TODO: Assert (item? prog)
  (c/isolate-state
   {:current-programm (bind-item prog)
    :result nil
    :continuation (bind-continuation prog)}
   (c/dynamic
    (fn [st]
      (if (:result st)
         ;; We have result, so go further down
         (let [next ((:continuation st) (:result st))]
           ;; Overwrite local-state with result of cont to recur
           ;; TODO: Cannot just return, need to give item
           ;; TODO: once ironically only runs once
           (c/fragment
            (c/once #(c/return :state {:current-programm (bind-item next)
                                       :result nil
                                       :continuation (bind-continuation next)}))))
         ;; We don't have result, display prog until commit arrives
         (c/handle-action
          (:current-programm st)
          (fn [_ ac]
            (if (commit? ac)
              (c/return :state (assoc st :result (commit-payload ac)))))))))))

(defn evil-runner [prog]
  ;; TODO: Assert (item? prog)
  (c/isolate-state
   {:item (bind-item prog)
    :continuation (bind-continuation prog)}
   (c/dynamic
    (fn [st]
      (c/handle-action
       ;; TODO: rework to use bind? to check for final item in state
       (if (:item st)
          (:item st)
          st)
       (fn [_ ac]
         (if (commit? ac)
           (c/return :state ((:continuation st) (commit-payload ac))))))))))


