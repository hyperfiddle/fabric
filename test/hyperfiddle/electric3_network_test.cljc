(ns hyperfiddle.electric3-network-test
  (:require [clojure.test :as t]
            [contrib.data :refer [->box]]
            [hyperfiddle.electric-local-def3 :as l]
            [hyperfiddle.electric3 :as e]
            [hyperfiddle.incseq :as i]
            [hyperfiddle.kvs :as kvs]
            [missionary.core :as m])
  #?(:cljs (:require-macros [hyperfiddle.electric3-network-test :refer [with-electric]]))
  (:import [missionary Cancelled]))

(declare tap step)
(defmacro with-electric [[tap step] opts eform & body]
  `(let [ngn# (l/->engine ~opts), ~tap #(l/tap ngn# %), ~step #(l/step ngn# %)]
     (l/spawn ngn# (l/local-ngn ~opts ngn# ~eform))
     ~@body
     (l/cancel ngn#)
     (t/is (~'thrown? Cancelled (l/step ngn# identity)))))

(t/deftest simple-transfer
  (with-electric [tap step] {} (tap (e/server 1))
    (step #{1})))

(t/deftest there-and-back
  (with-electric [tap step] {} (tap (e/server (e/call (e/fn [] (e/client 2)))))
    (step #{2})))

(declare foo)
(t/deftest binding-foo
  (with-electric [tap step] {} (tap (binding [foo 2] (e/server (e/call (e/fn [] (e/client (identity foo)))))))
    (step #{2})))

(declare Bar1)
(t/deftest binding-foo-Bar
  (with-electric [tap step] {} (tap (binding [Bar1 (e/fn [] (e/client (identity foo))), foo 2]
                                      (e/server (e/call Bar1))))
    (step #{2})))

(declare state)
(t/deftest nested-efor-with-transfer
  (let [!state (atom [1])]
    (with-electric [tap step] {} (binding [state (e/watch !state)]
                                   (e/for-by identity [x (e/server state)]
                                     (e/for-by identity [y (e/server state)]
                                       (tap [x y]))))
      (step #{[1 1]})
      (reset! !state [3])
      (step #{[3 3]}))))

(t/deftest fn-destructuring
  (with-electric [tap step] {} (do (tap (e/client ((fn [{:keys [a] ::keys [b]}] [::client a b]) {:a 1 ::b 2})))
                                   (tap (e/server ((fn [{:keys [a] ::keys [b]}] [::server a b]) {:a 1 ::b 2}))))
    (step #{[::client 1 2]})
    (step #{[::server 1 2]})))

(t/deftest switch
  (let [!x (atom true)]
    (with-electric [tap step] {} (let [x (e/watch !x)] (tap (if x (e/server [:server x]) [:client x])))
      (step #{[:server true]})
      (swap! !x not)
      (step #{[:client false]}))))

(t/deftest push-on-both
  (with-electric [tap step] {} (let [foo 1] (tap foo) (tap (e/client (identity foo))))
    (step #{1})
    (step #{1})))

(t/deftest efor-with-remote-body
  (let [!offset (atom 0)]
    (with-electric [tap step] {} (e/for [j (let [o (e/watch !offset)]
                                             (e/diff-by identity
                                               (range o (+ o 2))))]
                                   (e/server (tap j)))
      (step #{0 1})
      (step #{0 1})
      (swap! !offset inc)
      (step #{2}))))

(defn mount-at [kvs k v]
  (m/observe
    (fn [!]
      (! (i/empty-diff 0))
      (kvs/insert! kvs k v)
      #(kvs/remove! kvs k))))

(t/deftest mount-point
  (with-electric [tap step] {} (let [mp (e/mount-point)]
                                 (tap (e/as-vec (e/join mp)))
                                 (e/server
                                   (e/call
                                     (e/fn []
                                       (e/client
                                         [(e/join (mount-at mp (e/tag) :foo))
                                          (e/join (mount-at mp (e/tag) :bar))])))))
    (step #{[]})
    (step #{[:foo :bar]})))

(t/deftest branch-unmount
  (let [!x (atom true)]
    (with-electric [tap step] {} (if (e/watch !x) (e/server (tap :branch)) (tap :unmount))
      (step #{:branch})
      (swap! !x not) (step #{:unmount})
      (swap! !x not) (step #{:branch})
      (swap! !x not) (step #{:unmount}))))

(t/deftest server-client-server
  (with-electric [tap step] {} (e/server (tap (e/client (identity (e/server :foo)))))
    (step #{:foo})))

;; does not repro nondet bug found and fixed by Leo (electric3 test with clocks near the end)
(t/deftest do-not-dispose-convicted-outputs
  (dotimes [_ 10]
    (let [!x (atom 0)
          !y (atom true)
          !z (atom true)]
      (with-electric [tap step] {} (let [x (e/watch !x)]
                                     (tap (when (e/watch !y)
                                            (e/server [(identity x)
                                                       (when (e/watch !z) x)]))))
        (step #{[0 0]})
        (swap! !z not)
        (step #{[0 nil]})
        (swap! !y not)
        (step nil?)
        ))))

(comment
  (let [<s> (->box)]
    (dotimes [_ 100]
      (let [q (queue), ngn (l/->engine {})]
        (try (l/spawn ngn (l/local-ngn {} ngn (q (e/server (e/call (e/fn [] (e/client 2)))))))
             (l/step ngn)
             (q)
             (catch #?(:clj Throwable :cljs :default) e
               (let [[n0] (<s>), {:keys [steps seed]} (l/->info ngn)]
                 (when (or (nil? n0) (< steps n0)) (<s> [steps seed])))))))
    (<s>))
  )