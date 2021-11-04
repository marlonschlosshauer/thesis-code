(ns code.tests.future
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [code.tests.current :as curr]))

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
               (dom/button {:onclick (fn [state action] (c/return :action (b/make-commit-action inner)))
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
               (dom/button {:onclick (fn [state action] (c/return :action (b/make-commit-action)))
                            :class "confirm-button"
                            :disabled (not (== (:entered-code inner) (:code inner)))}
                           "Send code"))))))

(c/def-item done
  (dom/h2 "Successfully registered!"))

(c/def-item main
  (b/bind personal-info
         (fn [personal]
           (b/bind create-and-send-verification-code
                    (fn [code]
                      (dom/div
                       (dom/h2 (pr-str (merge personal code)))
                       done))))))

(comment
  (c/def-item retrofit-signup-process
    ;; Assert  failed: (base/item? item)
    ;; Erwartet curr/personal-info
    ;; Bekommt  (curr/personal-info)
    ;; TODO: bind! mit beiden kompatible machen (?)
  (b/bind (b/action->commit-action curr/personal-info)
           (fn [personal]
             (b/bind (b/action->commit-action curr/create-and-send-verification-code)
                      (fn [code]
                        (dom/div
                         (dom/h2 (pr-str (merge personal code)))
                         done)))))))
