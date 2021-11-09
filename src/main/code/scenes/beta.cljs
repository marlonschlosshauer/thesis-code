(ns code.scenes.beta
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.next :as next]))

(c/def-item personal-info
  (c/local-state
   {:name "" :email ""}
   (c/dynamic
    (fn [[outter inner]]
      (dom/div {:class "personal-info-container"}
               (dom/h4 "Personal Info:")
               (dom/samp (pr-str inner))
               (dom/input {:placeholder "name"
                           :value (:name inner)
                           :onChange (fn [[outter inner] e] (c/return :state [outter (assoc inner :name (.. e -target -value))]))}) (dom/input {:placeholder "email"
                           :value (:email inner)
                           :onChange (fn [[outter inner] e] (c/return :state [outter (assoc inner :email (.. e -target -value))]))})
               (dom/button {:onclick (fn [state action] (c/return :action (next/make-commit inner)))
                            :class "confirm-button"
                            :disabled (or (< (count (:name inner)) 1) (< (count (:email inner)) 1))}
                           "Continue"))))))

(defn generate-code []
  "A8J22")

(c/def-item create-and-send-verification-code
  (c/local-state
   {:code (generate-code) :entered-code ""}
   (c/dynamic
    (fn [[outter inner]]
      (dom/div {:class "verification-container"}
               (dom/h4 "Verification Info:")
               (dom/samp (pr-str inner))
               (dom/input {:placeholder "Code"
                           :value (:entered-code inner)
                           :onChange (fn [[outter inner] e] (c/return :state [outter (assoc inner :entered-code (.. e -target -value))]))})
               (dom/button {:onclick (fn [state action] (c/return :action (next/make-commit)))
                            :class "confirm-button"
                            :disabled (not (== (:entered-code inner) (:code inner)))}
                           "Send code"))))))

(c/def-item done
  (dom/h2 "Successfully registered!"))

(c/def-item signup-process
  (next/evil-runner
   (next/make-bind personal-info
                   (fn [personal]
                     (next/make-bind create-and-send-verification-code
                                     (fn [code] (dom/div done)))))))

(comment
  (let [verification (next/make-bind create-and-send-verification-code
                                  (fn [code] (dom/div done)))]
  ((next/bind-continuation verification) "")))

(c/def-item not-monster
  (c/isolate-state
   {:curr "a" :result nil}
   (c/dynamic
    (fn [st]
      (dom/div
       (dom/h2 (pr-str (:result st)))
       (dom/button {:onclick (fn [st ac] (c/return :state (assoc st :result(.now js/Date))))}
                   "Send it"))))))


(c/def-item main
  signup-process
  ;;not-monster
  )
