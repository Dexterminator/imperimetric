(ns clj.convert-test
  (:require [clojure.test :refer :all]
            [imperimetric.convert :refer [convert-text parse-text]]
            [imperimetric.handler :refer [handler]]
            [ring.mock.request :refer [request header]]
            [imperimetric.util :refer [map-all-to]]
            [clojure.string :as str]))

(def us-text (str "Four cups sugar, 1 1/2 Fluid Ounces lime, 5 tbsps salt, Twenty-five teaspoons pepper, "
                  "½ gallon water, three pints beer, 2 quarts milk, nine miles away, 3 yards away, "
                  "2 1/2 feet away, 2 inches away, 2 pounds and 2 ounces."))
(def metric-text (str "Four liters sugar, .90 Decilitres lime, 5 cL salt, Twenty-five ml pepper, nine km away, "
                      "9 meters away, 2 centimetres away, 120 millimetre away, 2 kg, 2 hg, 2 Grams, 2 milligrams."))

(deftest map-all-to-empty
  (is (= (map-all-to [] "test") {})))

(deftest map-all-to-common (is (= (map-all-to [:recipe :token :word] "test") {:recipe "test"
                                                                              :token  "test"
                                                                              :word   "test"})))

(deftest convert-empty
  (is (= (convert-text "" :us :metric) nil)))

(deftest precision
  (is (= (convert-text "42195 m, 42.195 km" :metric :us)
         "46145 yards, 26.219 miles")))

(deftest convert-singular
  (is (= (convert-text "0.918 m" :metric :us)
         "1.00 yard")))

(deftest convert-combined
  (is (= (convert-text "6 feet 4 ½ in tall, weight: 8 lb 3 oz." :us :metric))
      "1.94 m tall, weight: 3.71 kg."))

; US customary units
(deftest us->metric
  (is (= (convert-text us-text :us :metric)
         (str "9.46 dl sugar, 4.44 cl lime, 73.9 ml salt, 123 ml pepper, 1.89 l water, 1.42 l beer, 1.89 l milk, 14.5 km"
              " away, 2.74 m away, 0.762 m away, 5.08 cm away, 0.907 kg and 56.7 g."))))

(deftest us->imperial
  (is (= (convert-text us-text :us :imperial)
         (str "3.33 cups sugar, 1.56 fl. oz lime, 4.93 tbsp salt, 24.6 tsp pepper, 0.416 gallons water, 2.50 pints beer, 1.67"
              " quarts milk, nine miles away, 3 yards away, 2 1/2 feet away, 2 inches away, 2 pounds and 2 ounces."))))

; Imperial
(deftest imperial->metric
  (is (= (convert-text us-text :imperial :metric)
         (str "11.4 dl sugar, 4.26 cl lime, 75 ml salt, 125 ml pepper, 2.27 l water, 1.70 l beer,"
              " 2.27 l milk, 14.5 km away, 2.74 m away, 0.762 m away, 5.08 cm away, 0.907 kg and 56.7 g."))))

(deftest imperial->us
  (is (= (convert-text us-text :imperial :us)
         (str "4.80 cups sugar, 1.44 fl. oz lime, 5.07 tbsp salt, 25.4 tsp pepper, 0.600 gallons water, 3.60 pints beer,"
              " 2.40 quarts milk, nine miles away, 3 yards away, 2 1/2 feet away, 2 inches away, 2 pounds and 2 ounces."))))

; Metric
(deftest metric->us
  (is (= (convert-text metric-text :metric :us)
         (str "8.45 pints sugar, 0.380 cups lime, 1.69 fl. oz salt, 5.07 tsp pepper, 5.59 miles away,"
              " 9.84 yards away, 0.787 inches away, 4.72 inches away, 4.41 pounds, 7.05 oz, 0.0705 oz, 0.0000705 oz."))))

(deftest metric->imperial
  (is (= (convert-text metric-text :metric :imperial)
         (str "7.04 pints sugar, 0.317 cups lime, 1.76 fl. oz salt, 5 tsp pepper, 5.59 miles"
              " away, 9.84 yards away, 0.787 inches away, 4.72 inches away, 4.41 pounds, 7.05 oz, 0.0705 oz, 0.0000705 oz."))))

(deftest api-convert
  (is (= (handler (request :get "/convert" {"text" "1 fl. oz of water."
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  200
          :headers {"Content-Type" "application/json;charset=UTF-8"
                    "Vary"         "Accept"}
          :body    "{\"converted-text\":\"2.96 cl of water.\",\"original-text\":\"1 fl. oz of water.\"}"})))

(deftest api-uri-too-long
  (is (= (handler (request :get "/convert" {"text" (str/join (repeat 3000 "a"))
                                            "from" "us"
                                            "to"   "metric"}))
         {:status  414
          :headers {"Content-Type" "text/plain;charset=UTF-8"}
          :body    "Request URI too long."})))
