(ns app.views.testhttp
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom]
            [app.views.util :as u]
            [code.bind :as b]
            [ajax.core :as ajax])
  (:require-macros [code.macros :as m]))


(defrecord Request [f uri options])
(defrecord Response [ok? value])

(defn make-response [ok? value]
  (->Response ok? value))

(defn make-request [f uri options]
  (->Request f uri options))

(defn request? [r]
  (instance? Request r))

(defn- execute-request! [request handler]
  (let [{f :f uri :uri options :options} request
        conv (or (:convert-response options) identity)
        nopts (-> options
                  (assoc :handler
                         (fn [response]
                           (handler (conv (make-response true response))))
                         :error-handler
                         (fn [error]
                           (when (not= :aborted (:failure error))
                             (handler (conv (make-response false error))))))
                  (dissoc :convert-response))]
    (f uri nopts)))

(c/defn-subscription execute deliver! [request]
  (let [id (execute-request! request deliver!)]
    (fn []
      (ajax/abort id))))


(defn fetch-once
  [req f]
  (assert (request? req) req)
  (-> (execute req)
      (c/handle-action f)))

(defn get-random [min max]
  (dom/div
   (fetch-once
    (make-request
     ajax/GET
     (str "https://www.random.org/integers/?num=1&min="  min "&max=" max"&col=1&base=10&format=plain&rnd=new")
     nil)
    (fn [state response & rest]
      (c/return :action (b/make-commit (js/parseInt (:value response))))
      ))))


(def test-http
  (m/runner [first (b/return (get-random 1 6))
             _ (b/return (u/named-click-me! (str "First result:" first ", click to continue!")))
             second (b/return (get-random 7 9))
             _ (b/return (dom/div (str "Results: " (pr-str [first second]))))]))

(def main
  test-http)

