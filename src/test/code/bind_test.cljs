(ns code.bind-test
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [reacl-c.dom :as dom]
            [code.bind :as core]))

(deftest return-creates-prog
  (is (core/prog? (core/return (dom/h2 "return-creates-prog")))))
