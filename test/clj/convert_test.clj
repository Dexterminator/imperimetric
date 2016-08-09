(ns clj.convert-test
  (:require [clojure.test :refer :all]
            [imperimetric.convert :refer [convert-text parse-text]]
            [imperimetric.handler :refer [handler]]
            [ring.mock.request :refer [request header]]
            [imperimetric.util :refer [map-all-to]]
            [clojure.string :as str]))

(def us-text (str "Four cups sugar, 1 1/2 Ounces lime, 5 tbsps salt, Twenty-five teaspoons pepper, "
                  "½ gallon water, three pints beer, 2 quarts milk, nine miles away, 3 yards away, "
                  "2 1/2 feet away, 2 inches away."))
(def metric-text (str "Four liters sugar, 1 1/2 Decilitres lime, 5 cL salt, Twenty-five ml pepper, nine km away, "
                      "9 meters away, 2 centimetres away, 1 millimetre away."))

(deftest map-all-to-empty
  (is (= (map-all-to [] "test") {})))

(deftest map-all-to-common (is (= (map-all-to [:recipe :token :word] "test") {:recipe "test"
                                                                              :token  "test"
                                                                              :word   "test"})))

(deftest convert-empty
  (is (= (convert-text "" :us :metric) nil)))

; US customary units
(deftest us->metric
  (is (= (convert-text us-text :us :metric)
         (str "9.5 dl sugar, 4.4 cl lime, 73.9 ml salt, 123.2 ml pepper, 1.9 l water, 1.4 l beer, 1.9 l milk, 14.5 km"
              " away, 2.7 m away, 0.8 m away, 5.1 cm away."))))

(deftest us->imperial
  (is (= (convert-text us-text :us :imperial)
         (str "3.3 cups sugar, 1.6 oz lime, 4.9 tbsp salt, 24.6 tsp pepper, 0.4 gallons water, 2.5 pints beer, 1.7"
              " quarts milk, 9 miles away, 3 yards away, 2.5 feet away, 2 inches away."))))

; Imperial
(deftest imperial->metric
  (is (= (convert-text us-text :imperial :metric))
      "11.4 dl sugar, 4.3 cl lime, 75.0 ml salt, 125.0 ml pepper 2.3 l water, 1.7 l beer, 2.3 l milk, 14.5 km away."))

(deftest imperial->us
  (is (= (convert-text us-text :imperial :us))
      "4.8 cups sugar, 1.4 oz lime, 5.1 tbsp, 25.4 tsp pepper, 0.6 gallons water, 3.6 pints beer, 2.4 quarts milk, 9 miles away."))

; Metric
(deftest metric->us
  (is (= (convert-text metric-text :metric :us))
      "16.9 cups sugar, 0.6 cups lime, 1.7 oz salt, 5.1 tsp pepper, 5.6 miles away."))

(deftest metric->imperial
  (is (= (convert-text metric-text :metric :imperial))
      "14.1 cups sugar, 0.5 cups lime, 1.8 oz salt, 5.0 tsp pepper, 5.6 miles away."))

(deftest api-convert
  (is (= (handler (request :get "/convert" {"text" "1 oz of water."
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  200
          :headers {"Content-Type" "text/plain;charset=UTF-8"
                    "Vary"         "Accept"}
          :body    "3 cl of water."})))

(deftest api-uri-too-long
  (is (= (handler (request :get "/convert" {"text" (str/join (repeat 2000 "a"))
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  414
          :headers {"Content-Type" "text/plain;charset=UTF-8"}
          :body    "Request URI too long."})))
