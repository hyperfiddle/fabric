#!/bin/sh

echo "Running NodeJS tests"
./node_modules/.bin/shadow-cljs -A:test compile :test --force-spawn
node out/node-tests.js
