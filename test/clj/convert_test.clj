(ns clj.convert-test
  (:require [clojure.test :refer :all]
            [imperimetric.convert :refer [convert-recipe parse-recipe]]
            [imperimetric.handler :refer [handler]]
            [ring.mock.request :refer [request header]]
            [imperimetric.util :refer [map-all-to]]
            [clojure.string :as str]))

(def daquiri "1 1/2 oz White rum, 1/2 oz Simple syrup, 1 oz Lime juice.")
(def daquiri-metric "4.5 cl White rum, 1.5 cl Simple syrup, 3 cl Lime juice.")
(def margarita (str "Lime wedge, plus 2 lime wheels for garnish\n1 tablespoon coarse salt,"
                    " for glass rims\n4 ounces high quality blanco tequila (see note above)\n2"
                    " ounces Cointreau\n1 1/2 ounces fresh juice from 2 limes"))

(deftest map-all-to-empty
  (is (= (map-all-to [] "test") {})))

(deftest map-all-to-common (is (= (map-all-to [:recipe :token :word] "test") {:recipe "test"
                                                                              :token  "test"
                                                                              :word   "test"})))
(deftest convert-empty
  (is (= (convert-recipe "" :us :metric) nil)))

(deftest us->metric-fluid
  (is (= (convert-recipe daquiri :us :metric) "4.4 cl White rum, 1.5 cl Simple syrup, 3.0 cl Lime juice.")))

(deftest us->metric-fluid-tbsp
  (is (= (convert-recipe margarita :us :metric)
         (str "Lime wedge, plus 2 lime wheels for garnish\n14.8 ml coarse salt,"
              " for glass rims\n11.8 cl high quality blanco tequila (see note above)\n5.9"
              " cl Cointreau\n4.4 cl fresh juice from 2 limes"))))

(deftest metric->us-fluid
  (is (= (convert-recipe daquiri-metric :metric :us) "1.5 oz White rum, 0.5 oz Simple syrup, 1.0 oz Lime juice.")))

(deftest api-convert
  (is (= (handler (request :get "/convert" {"text" "1 oz of water."
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  200
          :headers {"Content-Type" "text/plain;charset=UTF-8"
                    "Vary"         "Accept"}
          :body    "3.0 cl of water."})))

(deftest api-uri-too-long
  (is (= (handler (request :get "/convert" {"text" (str/join (repeat 2000 "a"))
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  414
          :headers {"Content-Type" "text/plain;charset=UTF-8"}
          :body    "Request URI too long."})))
