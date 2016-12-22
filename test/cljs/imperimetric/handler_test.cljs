(ns imperimetric.handler-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [imperimetric.handlers :refer [make-ounces-fluid]]
            [pjstadig.humane-test-output]))

(deftest make-ounces-fluid-test
  (testing "make-ounces-fluid"
    (testing "converts 'oz' to 'fl. oz'"
      (is (= "1000 fl. oz, 3 fl. oz"
             (make-ounces-fluid "1000 oz, 3 oz"))))
    (testing "does not convert if text contains fluid ounces"
      (is (= "1000 fl. oz, 3 oz"
             (make-ounces-fluid "1000 fl. oz, 3 oz"))))))
