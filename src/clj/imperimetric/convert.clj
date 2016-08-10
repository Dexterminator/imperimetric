(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]
            [frinj.ops :refer [fj]]))

(def base-grammar "src/clj/imperimetric/base-grammar.bnf")

(defn make-parser [grammar-file]
  (insta/parser (str (slurp base-grammar) (slurp grammar-file))))

(def parsers
  {:metric   (make-parser "src/clj/imperimetric/metric-grammar.bnf")
   :us       (make-parser "src/clj/imperimetric/us-grammar.bnf")
   :imperial (make-parser "src/clj/imperimetric/us-grammar.bnf")})

(def numeral->int
  {"one"       1 "two" 2 "three" 3 "four" 4 "five" 5 "six" 6 "seven" 7 "eight" 8 "nine" 9
   "ten"       10 "eleven" 11 "twelve" 12 "thirteen" 13 "fourteen" 14 "fifteen" 15 "sixteen" 16
   "seventeen" 17 "eighteen" 18 "nineteen" 19 "twenty" 20 "thirty" 30 "forty" 40 "fifty" 50
   "sixty"     60 "seventy" 70 "eighty" 80 "ninety" 90})

(def unicode->fraction
  {"¼" "1/4", "½" "1/2", "¾" "3/4", "⅓" "1/3", "⅔" "2/3", "⅕" "1/5", "⅖" "2/5"})

(def unit->suffix
  {:liter  "l" :dl "dl" :cl "cl" :ml "ml" :km "km" :meter "m" :dm "dm" :cm "cm" :mm "mm"
   :floz   "fl. oz" :brfloz "fl. oz" :cup "cups" :brcup "cups" :tbsp "tbsp" :brtablespoon "tbsp" :tsp "tsp" :brtsp "tsp"
   :gallon "gallons" :brgallon "gallons" :pint "pints" :brpint "pints" :quart "quarts" :brquart "quarts"
   :mile   "miles" :yard "yards" :foot "feet" :inch "inches" :pound "pounds" :oz "oz"})

(defn parse-text [text from-system]
  ((parsers from-system) text))

(defn decimal-round [n]
  (let [v (format "%.1f" n)
        dec-v (read-string v)
        rounded-v (Math/round dec-v)]
    (if (= dec-v (double rounded-v))
      (str rounded-v)
      v)))

(defn convert-units [from to q]
  (double (:v (fj q from :to to))))

(defn convert-str [from to quantity]
  (str (decimal-round (convert-units from to quantity)) " " (unit->suffix to)))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

;; US customary units
(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str :cup :dl q))
(defmethod convert [:us :metric :floz] [_ _ q _] (convert-str :floz :cl q))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str :tbsp :ml q))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str :tsp :ml q))
(defmethod convert [:us :metric :gallon] [_ _ q _] (convert-str :gallon :liter q))
(defmethod convert [:us :metric :pint] [_ _ q _] (convert-str :pint :liter q))
(defmethod convert [:us :metric :quart] [_ _ q _] (convert-str :quart :liter q))
(defmethod convert [:us :metric :mile] [_ _ q _] (convert-str :mile :km q))
(defmethod convert [:us :metric :yard] [_ _ q _] (convert-str :yard :meter q))
(defmethod convert [:us :metric :foot] [_ _ q _] (convert-str :foot :meter q))
(defmethod convert [:us :metric :inch] [_ _ q _] (convert-str :inch :cm q))

(defmethod convert [:us :imperial :cup] [_ _ q _] (convert-str :cup :brcup q))
(defmethod convert [:us :imperial :floz] [_ _ q _] (convert-str :floz :brfloz q))
(defmethod convert [:us :imperial :tablespoon] [_ _ q _] (convert-str :tbsp :brtablespoon q))
(defmethod convert [:us :imperial :teaspoon] [_ _ q _] (convert-str :tsp :brtsp q))
(defmethod convert [:us :imperial :gallon] [_ _ q _] (convert-str :gallon :brgallon q))
(defmethod convert [:us :imperial :pint] [_ _ q _] (convert-str :pint :brpint q))
(defmethod convert [:us :imperial :quart] [_ _ q _] (convert-str :quart :brquart q))
(defmethod convert [:us :imperial :mile] [_ _ q _] (convert-str :mile :mile q))
(defmethod convert [:us :imperial :yard] [_ _ q _] (convert-str :yard :yard q))
(defmethod convert [:us :imperial :foot] [_ _ q _] (convert-str :foot :foot q))
(defmethod convert [:us :imperial :inch] [_ _ q _] (convert-str :inch :inch q))

;; Imperial
(defmethod convert [:imperial :metric :cup] [_ _ q _] (convert-str :brcup :dl q))
(defmethod convert [:imperial :metric :floz] [_ _ q _] (convert-str :brfloz :cl q))
(defmethod convert [:imperial :metric :tablespoon] [_ _ q _] (convert-str :brtablespoon :ml q))
(defmethod convert [:imperial :metric :teaspoon] [_ _ q _] (convert-str :brtsp :ml q))
(defmethod convert [:imperial :metric :gallon] [_ _ q _] (convert-str :brgallon :liter q))
(defmethod convert [:imperial :metric :pint] [_ _ q _] (convert-str :brpint :liter q))
(defmethod convert [:imperial :metric :quart] [_ _ q _] (convert-str :brquart :liter q))
(defmethod convert [:imperial :metric :mile] [_ _ q _] (convert-str :mile :km q))
(defmethod convert [:imperial :metric :yard] [_ _ q _] (convert-str :yard :meter q))
(defmethod convert [:imperial :metric :foot] [_ _ q _] (convert-str :foot :meter q))
(defmethod convert [:imperial :metric :inch] [_ _ q _] (convert-str :inch :cm q))

(defmethod convert [:imperial :us :cup] [_ _ q _] (convert-str :brcup :cup q))
(defmethod convert [:imperial :us :floz] [_ _ q _] (convert-str :brfloz :floz q))
(defmethod convert [:imperial :us :tablespoon] [_ _ q _] (convert-str :brtablespoon :tbsp q))
(defmethod convert [:imperial :us :teaspoon] [_ _ q _] (convert-str :brtsp :tsp q))
(defmethod convert [:imperial :us :gallon] [_ _ q _] (convert-str :brgallon :gallon q))
(defmethod convert [:imperial :us :pint] [_ _ q _] (convert-str :brpint :pint q))
(defmethod convert [:imperial :us :quart] [_ _ q _] (convert-str :brquart :quart q))
(defmethod convert [:imperial :us :mile] [_ _ q _] (convert-str :mile :mile q))
(defmethod convert [:imperial :us :yard] [_ _ q _] (convert-str :yard :yard q))
(defmethod convert [:imperial :us :foot] [_ _ q _] (convert-str :foot :foot q))
(defmethod convert [:imperial :us :inch] [_ _ q _] (convert-str :inch :inch q))

;; Metric
(defmethod convert [:metric :us :l] [_ _ q _] (convert-str :liter :pint q))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str :dl :cup q))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str :cl :floz q))
(defmethod convert [:metric :us :ml] [_ _ q _] (convert-str :ml :tsp q))
(defmethod convert [:metric :us :km] [_ _ q _] (convert-str :km :mile q))
(defmethod convert [:metric :us :m] [_ _ q _] (convert-str :meter :yard q))
(defmethod convert [:metric :us :dm] [_ _ q _] (convert-str :dm :feet q))
(defmethod convert [:metric :us :cm] [_ _ q _] (convert-str :cm :inch q))
(defmethod convert [:metric :us :mm] [_ _ q _] (convert-str :mm :inch q))

(defmethod convert [:metric :imperial :l] [_ _ q _] (convert-str :liter :brpint q))
(defmethod convert [:metric :imperial :dl] [_ _ q _] (convert-str :dl :brcup q))
(defmethod convert [:metric :imperial :cl] [_ _ q _] (convert-str :cl :brfloz q))
(defmethod convert [:metric :imperial :ml] [_ _ q _] (convert-str :ml :brtsp q))
(defmethod convert [:metric :imperial :km] [_ _ q _] (convert-str :km :mile q))
(defmethod convert [:metric :imperial :m] [_ _ q _] (convert-str :meter :yard q))
(defmethod convert [:metric :imperial :dm] [_ _ q _] (convert-str :dm :feet q))
(defmethod convert [:metric :imperial :cm] [_ _ q _] (convert-str :cm :inch q))
(defmethod convert [:metric :imperial :mm] [_ _ q _] (convert-str :mm :inch q))

(defn transform-map [from-system to-system]
  (merge
    (map-all-to [:integer :fraction :decimal] read-string)
    (map-all-to [:1-9 :10-19 :base] (comp numeral->int str/lower-case))
    (map-all-to [:base-with-suffix :mixed] +)
    {:text             str
     :measurement      (partial convert from-system to-system)
     :unicode-fraction unicode->fraction
     :unit             first}))

(defn convert-text [text from-system to-system]
  (let [parsed (parse-text text from-system)]
    (if (insta/failure? parsed)
      nil
      (str/join (insta/transform (transform-map from-system to-system) parsed)))))
