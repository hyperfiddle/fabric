#!/bin/sh -x

echo "Running Browser tests"
clojure -M:test:shadow-cljs compile :browser-test --force-spawn && \
./node_modules/.bin/karma start --single-run $@ # --browsers Chrome
