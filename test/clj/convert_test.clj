(ns clj.convert-test
  (:require [clojure.test :refer :all]
            [imperimetric.convert :refer [convert-recipe parse-recipe]]
            [imperimetric.handler :refer [handler]]
            [ring.mock.request :refer [request header]]
            [imperimetric.util :refer [map-all-to]]
            [clojure.string :as str]))

(def us-text "Four cups sugar, 1 1/2 Ounces lime, 5 tbsps salt, Twenty-five teaspoons pepper.")
(def metric-text "Four liters sugar, 1 1/2 Decilitres lime, 5 cL salt, Twenty-five ml pepper.")

(deftest map-all-to-empty
  (is (= (map-all-to [] "test") {})))

(deftest map-all-to-common (is (= (map-all-to [:recipe :token :word] "test") {:recipe "test"
                                                                              :token  "test"
                                                                              :word   "test"})))

(deftest convert-empty
  (is (= (convert-recipe "" :us :metric) nil)))

; US customary units
(deftest us->metric
  (is (= (convert-recipe us-text :us :metric)
         "9.5 dl sugar, 4.4 cl lime, 73.9 ml salt, 123.2 ml pepper.")))

(deftest us->imperial
  (is (= (convert-recipe us-text :us :imperial)
         "3.3 cups sugar, 1.6 oz lime, 4.9 tbsp salt, 24.6 tsp pepper.")))

; Imperial
(deftest imperial->metric
  (is (= (convert-recipe us-text :imperial :metric))
      "11.4 dl sugar, 4.3 cl lime, 75.0 ml salt, 125.0 ml pepper."))

(deftest imperial->us
  (is (= (convert-recipe us-text :imperial :us))
      "4.8 cups sugar, 1.4 oz lime, 5.1 tbsp, 25.4 tsp pepper."))

; Metric
(deftest metric->us
  (is (= (convert-recipe metric-text :metric :us))
      "16.9 cups sugar, 0.6 cups lime, 1.7 oz salt, 5.1 tsp pepper."))

(deftest metric->imperial
  (is (= (convert-recipe metric-text :metric :imperial))
      "14.1 cups sugar, 0.5 cups lime, 1.8 oz salt, 5.0 tsp pepper."))

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
