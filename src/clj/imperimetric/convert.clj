(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]
            [imperimetric.system-conversions :refer [convert convert-pounds-ounces convert-feet-inches convert-interval]]
            [imperimetric.precision :refer [significant-digits default-precision]]))

(def base-grammar "src/clj/imperimetric/base-grammar.bnf")

(defn make-parser [grammar-file]
  (insta/parser (str (slurp base-grammar) (slurp grammar-file))))

(defn get-parser [from to]
  (cond
    (= from :metric) (make-parser "src/clj/imperimetric/metric-grammar.bnf")
    (= to :metric) (make-parser "src/clj/imperimetric/english-metric-grammar.bnf")
    :else (make-parser "src/clj/imperimetric/english-english-grammar.bnf")))

(def numeral->int
  {"one"       1 "two" 2 "three" 3 "four" 4 "five" 5 "six" 6 "seven" 7 "eight" 8 "nine" 9
   "ten"       10 "eleven" 11 "twelve" 12 "thirteen" 13 "fourteen" 14 "fifteen" 15 "sixteen" 16
   "seventeen" 17 "eighteen" 18 "nineteen" 19 "twenty" 20 "thirty" 30 "forty" 40 "fifty" 50
   "sixty"     60 "seventy" 70 "eighty" 80 "ninety" 90})

(def unicode->fraction
  {"¼" "1/4", "½" "1/2", "¾" "3/4", "⅓" "1/3", "⅔" "2/3", "⅕" "1/5", "⅖" "2/5"})

(defn parse-text [text from-system to-system]
  ((get-parser from-system to-system) text))

(defn transform-map [from-system to-system]
  (merge
    (map-all-to [:integer :fraction] read-string)
    (map-all-to [:1-9 :10-19 :base] (comp numeral->int str/lower-case))
    (map-all-to [:base-with-suffix :mixed] +)
    {:decimal               bigdec
     :text                  str
     :measurement           (partial convert from-system to-system)
     :interval              (partial convert-interval from-system to-system)
     :unicode-fraction      unicode->fraction
     :implicit-zero-decimal (partial str "0")
     :unit                  first
     :pounds-ounces         convert-pounds-ounces
     :feet-inches           convert-feet-inches}))

(defn convert-text [text from-system to-system]
  (let [parsed (parse-text text from-system to-system)]
    (if (insta/failure? parsed)
      nil
      (str/join (insta/transform (transform-map from-system to-system) parsed)))))
