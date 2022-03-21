(ns app.views.testm

  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom]
            [app.views.util :as u]
            [code.bind :as b])
  (:require-macros [code.macros :as m]))

(defn item [name]
  (b/return (u/named-click-me! name)))

(def mix-primitives-macros
  (m/runner [a (item 1)
             b (b/then (item (inc a))
                       (fn [] (b/then (item (+ a 2))
                                      (fn [x] (item (inc x))))))
             c (item (+ a b))]
            (fn []
              (let [res (+ c 1)]
                (println res)
                res))))

(def implicit-state-leak-through-runner
  (c/local-state
   {:test "value"}
   (c/with-state-as state
     (dom/div
      (dom/p "outside of component")
      (dom/pre (pr-str state))
      (m/runner [a (b/return
                    (dom/div
                     (dom/p "inside of component:")
                     (u/wrap-state (c/fragment))))])))))

(defn stress-with-macro [n]
  (m/runner [a (item n) ;; selber rekursion aufrufen bis 100000
             _ (stress-with-macro (inc a))]))


(def emit-through
  ;; TODO: Does not work; runner handler catches
  ;; but does not propegate upwards, wrong assumption?
  (c/isolate-state
   42
   (c/dynamic
    (fn [state]
      (dom/div
       (dom/pre (pr-str state))
       (c/handle-action
        (m/runner [a (b/return
                      (dom/button {:onclick (fn [_ _] (c/return :action "test"))} "click-me!"))
                   b (b/return (dom/p "this shouldn't be happening'"))])
        (fn [state _]
          (println state)
          (c/return :state state))))))))

(def test-uneven-count-then
  (b/runner
   (m/then [a (item 1)
            ] (fn [])) ;; error removed to allow for compilation
   ))

(def test-end-expr-return-state
  (c/local-state
   1
   (c/dynamic
    (fn [[bla state]]
      ;; Timestamps differ between initial and second run
      ;; step #1 and #2 are not loaded from some cache
      ;; but instead generated again, after end-expr runs (and changes global state)
      (m/runner [a (item (str "#1 (Timestamp: " (.now js/Date) ")"))
                 b (item (str "#2 (Timestamp: " (.now js/Date) ")"))]
                (fn [[o i]]
                  (c/return :state [(assoc o :was-changed true) b])))))))

(def test-change-state-during-cause-reset
  ;; Step 2 changes outer state
  ;; (by emiting action that is caught in parent and handled)
  ;; which should cause reset
  ;; Result: It does not! -> change of outer-state does not always cause reset
  (c/local-state
   {:runs 0}
   (c/handle-action
    (m/runner [a (item 1)
               _ (b/return
                  (dom/button {:onclick (fn [_ _] (c/return :action :inc))} "send action upwards"))
               b (item 2)]
              (fn [[o i]]
                (c/return :state [(assoc o :runs 0) i])))
    (fn [[outer inner] ac]
      (if (= ac :inc)
        (c/return :state [outer (assoc inner :runs (inc (:runs inner)))])
        (c/return :action ac))))))


(def main
  (dom/div
     (dom/h2 "test end expr return state")
     test-end-expr-return-state
     ;;test-change-state-during-cause-reset
     ))


