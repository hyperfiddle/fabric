(ns contrib.str
  (:require clojure.pprint
            clojure.string
            [contrib.data :refer [orp]]
            [hyperfiddle.rcf :refer [tests]]))

(defn pprint-str [x]
  (with-out-str
    (clojure.pprint/with-pprint-dispatch
      clojure.pprint/code-dispatch
      (clojure.pprint/pprint x))))

(defn ^:deprecated includes-str? [v needle]
  ; perf - https://clojurians.slack.com/archives/C03RZMDSH/p1666290300539289
  (clojure.string/includes? (clojure.string/lower-case (str v))
                            (clojure.string/lower-case (str needle))))

(tests
  (includes-str? "alice" "e") := true
  (includes-str? "alice" "f") := false
  (includes-str? "alice" "") := true
  (includes-str? "alice" nil) := true
  (includes-str? nil nil) := true
  (includes-str? nil "") := true
  (includes-str? "" nil) := true)

;(defn empty->nil [s] (if (cuerdas.core/empty-or-nil? s) nil s))
;
;(tests
;  (empty->nil nil) := nil
;  (empty->nil "") := nil
;  (empty->nil " ") := " "
;  (empty->nil "a") := "a")

(defn blank->nil "Nullify empty strings, identity on all other values." [s]
  (if-not (string? s)
    s ; don't fail
    (if-not (clojure.string/blank? s) s nil)))

(tests
  (blank->nil nil) := nil
  (blank->nil "") := nil
  (blank->nil " ") := nil
  (blank->nil "      ") := nil
  (blank->nil "a") := "a"
  (not= (blank->nil "   a") nil) := true
  (not= (blank->nil "   a   ") nil) := true
  (blank->nil 1) := 1
  (blank->nil nil) := nil)

(defn or-str
  #_([& args] (apply orp seq args))                         ; can't apply macro todo
  ([a b] (orp seq a b))
  ([a b c] (orp seq a b c)))

(tests
  (or-str nil "b") := "b"
  (or-str "" "b") := "b"
  (or-str "a" "b") := "a"
  (or-str " " "b") := " ")