(ns code.bind
  (:require [reacl-c.core :as c :include-macros true]))

(defrecord CommitAction [payload])
(defn make-commit-action
  ([]
   (map->CommitAction {}))
  ([payload]
   (->CommitAction payload)))

(defn commit-action? [x] (instance? CommitAction x))
(defn commit-action-payload [ca] (:payload ca))

(defn action->commit-action [item]
  (c/handle-action
   item
   (fn [state msg]
     (c/return :state
               (if (commit-action? msg)
                 msg
                 (make-commit-action msg))))))

(defn bind [item handler]
  ;; Define local state to enable dynamic display of item
  (c/local-state
   {:next false :value nil}
   (c/dynamic
    (fn [[outter inner]]
      (c/handle-action
       (c/focus
        ;; Do not polute state with :next state
        (fn [] (or outter []))
        ;; Should we move to next step?
        (if (not (:next inner))
          item
          ;; TODO: Trampoline handler up!
          ;;(fn [] (handler (:value inner)))))
          (handler (:value inner))))
       (fn [[outter inner] msg]
         (c/return :state
                   (if (commit-action? msg)
                     [outter (assoc inner :next true :value (commit-action-payload msg))]
                     [outter inner]))))))))

(defn tra [thunk]
  (fn [args]
    (loop [result (thunk args)]
      (println {:origin "loop" :result result})
      (if (not (fn? result))
        result
        (recur (result))))))

(defn sbind [item handler]
  (fn []
    (bind item (fn [args] (handler args)))))


(defn lol-dec [x]
  (fn []
    (if (= x 0)
      x
      (lol-dec (dec x)))))

(defn lol-tra [x]
  (tra (lol-dec x)))

(defn donotcallthisfunction [x stamp]
  (if (= x 0)
    {:start stamp :end (.now js/Date)}
    (donotcallthisfunction (dec x) stamp)))

(comment
  (donotcallthisfunction 10000 (.now js/Date)))

(comment
  ((lol-tra 1000000000)))
