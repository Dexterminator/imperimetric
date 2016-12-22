(ns imperimetric.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [imperimetric.handler-test]))

(doo-tests 'imperimetric.handler-test)
