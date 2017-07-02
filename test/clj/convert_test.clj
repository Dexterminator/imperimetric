(ns clj.convert-test
  (:require [midje.sweet :refer :all]
            [imperimetric.convert.core :refer [convert-text]]
            [imperimetric.convert.parser :refer [parse-text]]
            [imperimetric.handler :refer [handler]]
            [ring.mock.request :refer [request header]]
            [imperimetric.util :refer [map-all-to]]
            [imperimetric.frinj-setup :refer [frinj-setup!]]
            [clojure.string :as str]))

(frinj-setup!)

(def us-text (str "Four cups sugar, 1 1/2 Fluid Ounces lime, 5 tbsps salt, Twenty-five teaspoons pepper, "
                  "½ gallon water, three pints beer, 2 quarts milk, 2 gill gin, nine miles away, 3 yards away, "
                  "2 1/2 feet away, 2 inches away, 2 pounds, 2 ounces, 4 tons, 1 square mile, 1 sq yd, 1 ft2, "
                  "1 sq inch, 1 acre, 1 mph, 1 ft/s, 45 degrees fahrenheit."))
(def metric-text (str "Four liters sugar, .90 Decilitres lime, 5 cL salt, Twenty-five ml pepper, nine km away, "
                      "9 meters away, 2 centimetres away, 120 millimetre away, 2 kg, 2 hg, 2 Grams, 2 milligrams,"
                      " 4 tons, 1 km2, 1 square meter, 5 square dm, 1 sq cm, 1mm^2, 1 hectare, 1 km/h, 1 m/s, 30°C."))
(def combined-text (str "1 mile 100 yards, 1mile 1 foot, 1 mile 1 inch, 1 yard 1 foot, 1 yard 1 ”,"
                        " 6 feet 4 ½ in tall, weight: 8 lb 3 oz."))

(facts "About map-all-to"
  (fact "empty vector gives empty map"
    (map-all-to [] "test") => {})
  (fact "correctly maps all input keywords to value"
    (map-all-to [:recipe :token :word] "test") => {:recipe "test"
                                                   :token  "test"
                                                   :word   "test"}))


(facts "About conversion properties"
  (fact "Empty conversion results in nil"
    (convert-text "" :us :metric) => nil)
  (fact "Conversions use correct precision (number of significant digits)"
    (convert-text "42195 m, 42.195 km" :metric :us) => "46145 yards, 26.219 miles")
  (fact "Conversions which result in exactly 1 use singular units"
    (convert-text "0.918 m" :metric :us) => "1.00 yard")
  (fact "Does not try to parse division by zero"
    (convert-text "1/0 oz." :us :metric) => "1/0 oz.")
  (fact "Converts units in parens"
    (convert-text "(1/4 oz)" :us :metric) "(7.09 g)")
  (fact "Converts interval"
    (convert-text "1-1 1/2 cup, 2-3 tbsps, 1-3 mi, 1-3 minutes." :us :metric) =>
    "237 ml-355 ml, 29.6 ml-44.4 ml, 1.61 km-4.83 km, 1-3 minutes.")
  (fact "Converts negative temperature"
    (convert-text "-5 degrees fahrenheit, -30F, -10.5F" :us :metric) => "-20.6°C, -34.4°C, -23.6°C")
  (fact "Does not convert negative non-temperature"
    (convert-text "-10 oz, -1 cup, -10miles, -12 mph" :us :metric) => "-10 oz, -1 cup, -10miles, -12 mph")
  (fact "English -> english does not convert temperature"
    (convert-text "-10F, -10.2F" :us :imperial) => "-10F, -10.2F")
  (fact "100 ml or more gets converted to cups"
    (convert-text "10 ml, 16 ml, 101 ml, 900 ml." :metric :us) => "2.03 tsp, 1.08 tbsp, 0.427 cups, 3.80 cups.")
  (fact "Fractions with infinite expansions use 3 significant digits"
    (convert-text "1/3 cup" :us :metric) => "78.9 ml")
  (fact "Newlines don't work in measurements"
    (convert-text "1 \noz" :us :metric) => "1 \noz")
  (fact "Newlines are allowed between measurements"
    (convert-text "1 oz salt\n1 mph." :us :metric) => "28.3 g salt\n1.61 km/h.")
  (fact "Dashes are ok in some measurements"
    (convert-text "Grease and flour an 8-inch square pan." :us :metric) => "Grease and flour an 20.3 cm square pan."))


(facts "About conversions from US customary units"
  (fact "Correctly converts to metric"
    (convert-text us-text :us :metric) =>
    (str "946 ml sugar, 4.44 cl lime, 73.9 ml salt, 123 ml pepper, 1.89 l water, 1.42 l beer, 1.89 l milk, 23.7 cl"
         " gin, 14.5 km away, 2.74 m away, 0.762 m away, 5.08 cm away, 0.907 kg, 56.7 g, 3.63 tonnes,"
         " 2.59 km², 0.836 m², 0.0929 m², 6.45 cm², 0.405 hectares, 1.61 km/h, 0.305 m/s, 7.22°C."))
  (fact "Correctly converts to Imperial"
    (convert-text us-text :us :imperial) =>
    (str "3.33 cups sugar, 1.56 fl. oz lime, 4.93 tbsp salt, 24.6 tsp pepper, 0.416 gallons water, 2.50 pints beer, 1.67"
         " quarts milk, 1.67 gills gin, nine miles away, 3 yards away, 2 1/2 feet away, 2 inches away, 2 pounds, 2"
         " ounces, 3.57 tons, 1 square mile, 1 sq yd, 1 ft2, 1 sq inch, 1 acre, 1 mph, 1 ft/s, 45 degrees fahrenheit.")))


(facts "About conversions from Imperial"
  (fact "Correctly converts to metric"
    (convert-text us-text :imperial :metric) =>
    (str "1140 ml sugar, 4.26 cl lime, 75 ml salt, 125 ml pepper, 2.27 l water, 1.70 l beer,"
         " 2.27 l milk, 28.4 cl gin, 14.5 km away, 2.74 m away, 0.762 m away, 5.08 cm away, 0.907 kg, 56.7 g,"
         " 4.06 tonnes, 2.59 km², 0.836 m², 0.0929 m², 6.45 cm², 0.405 hectares, 1.61 km/h, 0.305 m/s, 7.22°C."))
  (fact "Correctly converts to US customary units"
    (convert-text us-text :imperial :us) =>
    (str "4.80 cups sugar, 1.44 fl. oz lime, 5.07 tbsp salt, 25.4 tsp pepper, 0.600 gallons water, 3.60 pints beer,"
         " 2.40 quarts milk, 2.40 gills gin, nine miles away, 3 yards away, 2 1/2 feet away, 2 inches away, 2 pounds,"
         " 2 ounces, 4.48 tons, 1 square mile, 1 sq yd, 1 ft2, 1 sq inch, 1 acre, 1 mph, 1 ft/s, 45 degrees fahrenheit.")))


(facts "About conversions from metric"
  (fact "Correctly converts to US customary units"
    (convert-text metric-text :metric :us) =>
    (str "8.45 pints sugar, 0.380 cups lime, 1.69 fl. oz salt, 1.69 tbsp pepper, 5.59 miles away,"
         " 9.84 yards away, 0.787 inches away, 4.72 inches away, 4.41 pounds, 7.05 oz, 0.0705 oz, 0.0000705 oz,"
         " 4.41 tons, 0.386 sq mi, 1.20 sq yd, 0.538 sq ft, 0.155 sq in, 0.00155 sq in, 2.47 acres, 0.621 mph,"
         " 3.28 ft/s, 86°F."))
  (fact "Correctly converts to Imperial"
    (convert-text metric-text :metric :imperial) =>
    (str "7.04 pints sugar, 0.317 cups lime, 1.76 fl. oz salt, 1.67 tbsp pepper, 5.59 miles"
         " away, 9.84 yards away, 0.787 inches away, 4.72 inches away, 4.41 pounds, 7.05 oz, 0.0705 oz, 0.0000705"
         " oz, 3.94 tons, 0.386 sq mi, 1.20 sq yd, 0.538 sq ft, 0.155 sq in, 0.00155 sq in, 2.47 acres, 0.621 mph,"
         " 3.28 ft/s, 86°F.")))

(facts "About combined units"
  (fact "Combined units result in single correct converted unit"
    (convert-text combined-text :us :metric) =>
    "1.71 km, 1.61 km, 1.61 km, 1.22 m, 0.942 m, 1.94 m tall, weight: 3.71 kg."))


(facts "About api conversions"
  (fact "A typical api call returns success and correct json"
    (handler (request :get "/convert" {"text" "1 fl. oz of water."
                                       "from" "us"
                                       "to"   "metric"})) =>
    {:status  200
     :headers {"Content-Type" "application/json;charset=UTF-8"
               "Vary"         "Accept"}
     :body    "{\"converted-text\":\"2.96 cl of water.\",\"original-text\":\"1 fl. oz of water.\"}"})
  (fact "A very long URL results in a uri too long response"
    (handler (request :get "/convert" {"text" (str/join (repeat 3000 "a"))
                                       "from" "us"
                                       "to"   "metric"})) =>
    {:status  414
     :headers {"Content-Type" "text/plain;charset=UTF-8"}
     :body    "Request URI too long."})
  (fact "System arguments have to be us, imerial, or metric"
    (handler (request :get "/convert" {"text" "1 fl. oz of water"
                                       "from" "wat"
                                       "to" "metric"})) =>
    {:status  400
     :headers {"Content-Type" "text/plain;charset=UTF-8"}
     :body    "Bad request: Allowed systems are: \"us\", \"imperial\", and \"metric\"."}))
