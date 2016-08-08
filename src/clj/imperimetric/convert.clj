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

(defn parse-recipe [recipe from-system]
  ((parsers from-system) recipe))

(defn decimal-round [n]
  (format "%.1f" n))

(defn convert-units [from to q]
  (double (:v (fj q from :to to))))

(defn convert-str [from to quantity suffix]
  (str (decimal-round (convert-units from to quantity)) " " suffix))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

;; US customary units
(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str :cup :dl q "dl"))
(defmethod convert [:us :metric :oz] [_ _ q _] (convert-str :floz :cl q "cl"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str :tbsp :ml q "ml"))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str :tsp :ml q "ml"))
(defmethod convert [:us :metric :gallon] [_ _ q _] (convert-str :gallon :liter q "l"))
(defmethod convert [:us :metric :pint] [_ _ q _] (convert-str :pint :liter q "l"))
(defmethod convert [:us :metric :quart] [_ _ q _] (convert-str :quart :liter q "l"))

(defmethod convert [:us :imperial :cup] [_ _ q _] (convert-str :cup :brcup q "cups"))
(defmethod convert [:us :imperial :oz] [_ _ q _] (convert-str :floz :brfloz q "oz"))
(defmethod convert [:us :imperial :tablespoon] [_ _ q _] (convert-str :tbsp :brtablespoon q "tbsp"))
(defmethod convert [:us :imperial :teaspoon] [_ _ q _] (convert-str :tsp :brtsp q "tsp"))
(defmethod convert [:us :imperial :gallon] [_ _ q _] (convert-str :gallon :brgallon q "gallons"))
(defmethod convert [:us :imperial :pint] [_ _ q _] (convert-str :pint :brpint q "pints"))
(defmethod convert [:us :imperial :quart] [_ _ q _] (convert-str :quart :brquart q "quarts"))

;; Imperial
(defmethod convert [:imperial :metric :cup] [_ _ q _] (convert-str :brcup :dl q "dl"))
(defmethod convert [:imperial :metric :oz] [_ _ q _] (convert-str :brfloz :cl q "cl"))
(defmethod convert [:imperial :metric :tablespoon] [_ _ q _] (convert-str :brtablespoon :ml q "ml"))
(defmethod convert [:imperial :metric :teaspoon] [_ _ q _] (convert-str :brtsp :ml q "ml"))
(defmethod convert [:imperial :metric :gallon] [_ _ q _] (convert-str :brgallon :liter q "l"))
(defmethod convert [:imperial :metric :pint] [_ _ q _] (convert-str :brpint :liter q "l"))
(defmethod convert [:imperial :metric :quart] [_ _ q _] (convert-str :brquart :liter q "l"))

(defmethod convert [:imperial :us :cup] [_ _ q _] (convert-str :brcup :cup q "cups"))
(defmethod convert [:imperial :us :oz] [_ _ q _] (convert-str :brfloz :floz q "oz"))
(defmethod convert [:imperial :us :tablespoon] [_ _ q _] (convert-str :brtablespoon :tbsp q "tbsp"))
(defmethod convert [:imperial :us :teaspoon] [_ _ q _] (convert-str :brtsp :tsp q "tsp"))
(defmethod convert [:imperial :us :gallon] [_ _ q _] (convert-str :brgallon :gallon q "l"))
(defmethod convert [:imperial :us :pint] [_ _ q _] (convert-str :brpint :pint q "l"))
(defmethod convert [:imperial :us :quart] [_ _ q _] (convert-str :brquart :quart q "l"))

;; Metric
(defmethod convert [:metric :us :l] [_ _ q _] (convert-str :liter :cup q "cups"))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str :dl :cup q "cups"))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str :cl :floz q "oz"))
(defmethod convert [:metric :us :ml] [_ _ q _] (convert-str :ml :tsp q "tsp"))

(defmethod convert [:metric :imperial :l] [_ _ q _] (convert-str :liter :brcup q "cups"))
(defmethod convert [:metric :imperial :dl] [_ _ q _] (convert-str :dl :brcup q "cups"))
(defmethod convert [:metric :imperial :cl] [_ _ q _] (convert-str :cl :brfloz q "oz"))
(defmethod convert [:metric :imperial :ml] [_ _ q _] (convert-str :ml :brtsp q "tsp"))

(defn transform-map [from-system to-system]
  (merge
    (map-all-to [:recipe :token :word :whitespace] str)
    (map-all-to [:integer :fraction :decimal] read-string)
    (map-all-to [:quantity :numeral :20-99] identity)
    (map-all-to [:1-9 :10-19 :base] (comp numeral->int str/lower-case))
    (map-all-to [:base-with-suffix :mixed] +)
    {:measurement      (partial convert from-system to-system)
     :unicode-fraction unicode->fraction
     :unit             first}))

(defn convert-recipe [recipe from-system to-system]
  (let [parsed (parse-recipe recipe from-system)]
    (if (insta/failure? parsed)
      nil
      (str/join (insta/transform (transform-map from-system to-system) parsed)))))
