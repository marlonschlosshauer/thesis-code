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
  (m/runner [a (item n)
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

(def main
  (dom/div
   (dom/div
    (dom/h2 "mix primitives and macros")
    mix-primitives-macros)
   (dom/div
    (dom/h2 "implicit state leak through runner")
    implicit-state-leak-through-runner)
   (dom/div
    (dom/h2 "stress but with macros")
    (stress-with-macro 0))
   (dom/div
    (dom/h2 "emit through runner")
    emit-through)
   (dom/div
    (dom/h2 "test uneven count then macro")
    test-uneven-count-then)))
