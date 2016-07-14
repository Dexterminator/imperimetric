(ns imperimetric.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [imperimetric.core-test]))

(doo-tests 'imperimetric.core-test)
