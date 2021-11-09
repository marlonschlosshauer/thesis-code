(ns code.scenes.trampoline
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.main :as main]
            [reacl-c.dom :as dom]
            [code.bind :as b]
            [code.scenes.future :as future]))

(c/def-item main
  ((b/tra (b/sbind future/personal-info
                   (fn [personal]
                     (b/bind future/create-and-send-verification-code
                             (fn [code]
                               future/done)))))))
