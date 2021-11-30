(ns code.bind-test
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [reacl-c.dom :as dom]
            [reacl-c.test-util.core :as test-util]
            [reacl-c.test-util.core-testing :as core-testing]
            ;;[reacl-c.test-util.dom-testing :as dom-testing] ;; TODO: Fix dependency
            [reacl-c.test-util.test-renderer :as test-renderer]
            [code.bind :as core]))

(deftest return-creates-prog
  (is (core/prog? (core/return (dom/h2 "return-creates-prog")))))

(deftest bind-returns-prog
  (is (core/prog?
       (core/bind
        (core/return (dom/h2 "bind-returns-prog"))
        (fn [x] (core/return (dom/h2 "bind-returns-prog")))))))

(deftest bind-on-commit-call-continuation
  (async
   done
   (test-renderer/inject-action! (core/show (core/bind (dom/h2) (fn [x] (done)))) (core/make-commit {}))))

(deftest bind-returns-other-prog-after-commit
  (let [before (core/return (dom/h2 "before"))
        after (core/return (dom/h2 "after"))
        ;; Got Prog from bind
        ;; unwrap to item
        ;; inject action into item
        result (test-renderer/inject-action! (core/show (core/bind before (fn [x] (core/return after)))) (core/make-commit {}))]
    (is (and (core/prog? result) (core-testing/like? (core/show result) after)))))


(deftest bind-compose-left-hand
  ;; (Prog 1 >>= Prog 2) >>= Prog 3
  )

(deftest bind-compose-right-hand
  ;; Prog 1 >>= (Prog 2 >>= Prog 3)
  )

;; TODO:
;; Trampoline?
;; View change (Twitter reg etc.)
;; Recursive call
