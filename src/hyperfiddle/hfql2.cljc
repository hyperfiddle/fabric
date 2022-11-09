(ns hyperfiddle.hfql2
  (:require #?(:clj [hyperfiddle.hfql2.impl :as impl]))
  #?(:cljs (:require-macros [hyperfiddle.hfql2])))

(defmacro hfql
  ([form] (impl/hfql* &env [] form))
  ([bindings form] (impl/hfql* &env bindings form)))

