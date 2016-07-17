(ns clj.convert-test
  (:require [clojure.test :refer :all]
            [imperimetric.convert :refer [convert-recipe parse-recipe]]
            [imperimetric.util :refer [map-all-to]]))

(def daquiri "1 1/2 oz White rum, 1/2 oz Simple syrup, 1 oz Lime juice.")
(def daquiri-metric "4.5 cl White rum, 1.5 cl Simple syrup, 3 cl Lime juice.")
(def margarita (str "Lime wedge, plus 2 lime wheels for garnish\n1 tablespoon coarse salt,"
                    " for glass rims\n4 ounces high quality blanco tequila (see note above)\n2"
                    " ounces Cointreau\n1 1/2 ounces fresh juice from 2 limes"))

(deftest map-all-to-empty
  (is (= (map-all-to [] "test") {})))

(deftest map-all-to-common
  (is (= (map-all-to [:recipe :token :word] "test") {:recipe "test"
                                                     :token  "test"
                                                     :word   "test"})))
(deftest us->metric-fluid
  (is (= (convert-recipe daquiri :us :metric) "4.44 cl White rum, 1.48 cl Simple syrup, 2.96 cl Lime juice.")))

(deftest us->metric-fluid-tbsp
  (is (= (convert-recipe margarita :us :metric)
         (str "Lime wedge, plus 2 lime wheels for garnish\n1.48 cl coarse salt,"
              " for glass rims\n11.83 cl high quality blanco tequila (see note above)\n5.91"
              " cl Cointreau\n4.44 cl fresh juice from 2 limes"))))

(deftest metric->us-fluid
  (is (= (convert-recipe daquiri-metric :metric :us) "1.52 oz White rum, 0.51 oz Simple syrup, 1.01 oz Lime juice.")))
