(ns dustin.via5)

(fmap render (bind >route qr))
(fmap render (bind >route (fn [route] (qr route))))


(macroexpand '(via (let [route ~>route]
                     (qr route))))
:= '(bind >route (fn [route] (qr route)))


(macroexpand '(via (render ~(let [>x ~>route] (qr route)))))
:= '(fmap render (bind >route (fn [route] (qr route))))

; how it works
; bind rewrite rule:
'(let [route ~>route] ...)
:= (bind >route (fn [route] ...))

'(let [x 1 route ~>route] ...)
'(let [x 1]
   (let [route ~>route]
     ...))

(bind-1 1 (fn [x]
            (bind-2 >route (fn [route]
                             ...))))
:= '(let [x 1]
      (bind >route (fn [route] ...)))


;fmap case
(macroexpand '(via (f ~(g x) ~(h y))))
;; wrong way first:
:= '(bind (g x) (fn [%]
                  (bind (h y) (fn [%2]
                                (f % %2)))))

; better way: (if all exprs are RT)
:= '(fmap f (g x) (h y))




; complex case

(macroexpand '(via (render ~(let [>x ~>route] (qr route)))))
:=1 '(fmap render (let [a 1 >x ~>route] (qr route)))
:=2 '(fmap render (let [a 1]
                    (let [>x ~>route]
                      (qr route))))
:=3 '(fmap render (let [a 1]
                    (bind >route (fn [route] (qr route)))))



; how to compose reactors / asts

(defn render [xs]
  (via
    [:table
     [:tr xs]]))

(fn [s]
  [ 42 (assoc s 'x 10)])

(defn inc-with-state-effect [x]
  (set! *state)
  (inc x))

'(inc-with-state-effect (inc-with-state-effect 1))

(defn query-route [#_>$ [f & args :as route]]               ; :: Incr a
  (traced-reactor!
    (first route)
    (case f

      dustin.fiddle/submissions
      (let [[needle] args]
        (submissions ~>$ needle) #_(fmapI #(submissions % needle) >$))

      (m/watch (atom 404)))))

(defmacro via [& body]
  `(binding [*reactor* ...]
     ~@body))

(via (render ~(let [>x ~>route] (qr route))))


; via for
(macroexpand '(via (for [route ~>routes] (qr route))))

:= (bind (extend-seq :db/id >routes) (fn [routes>]
                                       (for [>route routes>]
                                         (bind :db/id >route (fn [route] (qr route))))))

:= (reactive-for :db/id >routes (fn [route] (qr route)))


; cp macro
(macroexpand '(cp (f (?! (g x)) (?! (h y)))))