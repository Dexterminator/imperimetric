(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]
            [frinj.ops :refer [fj]]))

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

(def unit->suffix
  {:liter  "l" :dl "dl" :cl "cl" :ml "ml" :km "km" :meter "m" :dm "dm" :cm "cm" :mm "mm" :kg "kg" :hg "hg" :g "g" :mg "mg"
   :floz   "fl. oz" :brfloz "fl. oz" :cup "cups" :brcup "cups" :tbsp "tbsp" :brtablespoon "tbsp" :tsp "tsp" :brtsp "tsp"
   :gallon "gallons" :brgallon "gallons" :pint "pints" :brpint "pints" :quart "quarts" :brquart "quarts"
   :mile   "miles" :yard "yards" :foot "feet" :inch "inches" :pound "pounds" :oz "oz" :gill "gills" :brgill "gills"
   :ton    "tons" :metricton "tonnes" :brton "tons"})

(defn singular [unit]
  (cond
    (#{:cup :brcup :gallon :brgallon :pint :brpint :quart :brquart :mile :yard} unit) (let [suffix (unit->suffix unit)]
                                                                                        (subs suffix 0 (dec (count suffix))))
    (= :foot unit) "foot"
    :else (unit->suffix unit)))

(def default-precision 3)

(defn parse-text [text from-system to-system]
  ((get-parser from-system to-system) text))

(defn significant-digits [n]
  (let [stripped (.stripTrailingZeros (with-precision 100 (bigdec n)))
        precision (.precision stripped)
        scale (.scale stripped)]
    (if (< scale 0)
      (- precision scale)
      precision)))

(defn convert-units [from to q]
  (let [q (rationalize q)
        digits (significant-digits q)
        precision (if (> digits default-precision) digits default-precision)]
    (with-precision precision (bigdec (:v (fj q from :to to))))))

(defn convert-str [from to quantity]
  (let [converted-quantity (convert-units from to quantity)
        suffix (if (= 1M converted-quantity)
                 (singular to)
                 (unit->suffix to))]
    (str (.toPlainString converted-quantity) " " suffix)))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

;; Functions used when unit is the same in us and imperial, to avoid duplication
(defn mile->metric [q] (convert-str :mile :km q))
(defn yard->metric [q] (convert-str :yard :meter q))
(defn foot->metric [q] (convert-str :foot :meter q))
(defn inch->metric [q] (convert-str :inch :cm q))
(defn pound->metric [q] (convert-str :pound :kg q))
(defn oz->metric [q] (convert-str :oz :g q))

(defn km->english [q] (convert-str :km :mile q))
(defn m->english [q] (convert-str :meter :yard q))
(defn dm->english [q] (convert-str :dm :feet q))
(defn cm->english [q] (convert-str :cm :inch q))
(defn mm->english [q] (convert-str :mm :inch q))
(defn kg->english [q] (convert-str :kg :pound q))
(defn hg->english [q] (convert-str :hg :oz q))
(defn g->english [q] (convert-str :g :oz q))
(defn mg->english [q] (convert-str :mg :oz q))

;; US customary units
(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str :cup :dl q))
(defmethod convert [:us :metric :floz] [_ _ q _] (convert-str :floz :cl q))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str :tbsp :ml q))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str :tsp :ml q))
(defmethod convert [:us :metric :gallon] [_ _ q _] (convert-str :gallon :liter q))
(defmethod convert [:us :metric :pint] [_ _ q _] (convert-str :pint :liter q))
(defmethod convert [:us :metric :quart] [_ _ q _] (convert-str :quart :liter q))
(defmethod convert [:us :metric :gill] [_ _ q _] (convert-str :gill :cl q))
(defmethod convert [:us :metric :mile] [_ _ q _] (mile->metric q))
(defmethod convert [:us :metric :yard] [_ _ q _] (yard->metric q))
(defmethod convert [:us :metric :foot] [_ _ q _] (foot->metric q))
(defmethod convert [:us :metric :inch] [_ _ q _] (inch->metric q))
(defmethod convert [:us :metric :pound] [_ _ q _] (pound->metric q))
(defmethod convert [:us :metric :oz] [_ _ q _] (oz->metric q))
(defmethod convert [:us :metric :ton] [_ _ q _] (convert-str :ton :metricton q))

(defmethod convert [:us :imperial :cup] [_ _ q _] (convert-str :cup :brcup q))
(defmethod convert [:us :imperial :floz] [_ _ q _] (convert-str :floz :brfloz q))
(defmethod convert [:us :imperial :tablespoon] [_ _ q _] (convert-str :tbsp :brtablespoon q))
(defmethod convert [:us :imperial :teaspoon] [_ _ q _] (convert-str :tsp :brtsp q))
(defmethod convert [:us :imperial :gallon] [_ _ q _] (convert-str :gallon :brgallon q))
(defmethod convert [:us :imperial :pint] [_ _ q _] (convert-str :pint :brpint q))
(defmethod convert [:us :imperial :quart] [_ _ q _] (convert-str :quart :brquart q))
(defmethod convert [:us :imperial :gill] [_ _ q _] (convert-str :gill :brgill q))
(defmethod convert [:us :imperial :ton] [_ _ q _] (convert-str :ton :brton q))

;; Imperial
(defmethod convert [:imperial :metric :cup] [_ _ q _] (convert-str :brcup :dl q))
(defmethod convert [:imperial :metric :floz] [_ _ q _] (convert-str :brfloz :cl q))
(defmethod convert [:imperial :metric :tablespoon] [_ _ q _] (convert-str :brtablespoon :ml q))
(defmethod convert [:imperial :metric :teaspoon] [_ _ q _] (convert-str :brtsp :ml q))
(defmethod convert [:imperial :metric :gallon] [_ _ q _] (convert-str :brgallon :liter q))
(defmethod convert [:imperial :metric :pint] [_ _ q _] (convert-str :brpint :liter q))
(defmethod convert [:imperial :metric :quart] [_ _ q _] (convert-str :brquart :liter q))
(defmethod convert [:imperial :metric :gill] [_ _ q _] (convert-str :brgill :cl q))
(defmethod convert [:imperial :metric :mile] [_ _ q _] (mile->metric q))
(defmethod convert [:imperial :metric :yard] [_ _ q _] (yard->metric q))
(defmethod convert [:imperial :metric :foot] [_ _ q _] (foot->metric q))
(defmethod convert [:imperial :metric :inch] [_ _ q _] (inch->metric q))
(defmethod convert [:imperial :metric :pound] [_ _ q _] (pound->metric q))
(defmethod convert [:imperial :metric :oz] [_ _ q _] (oz->metric q))
(defmethod convert [:imperial :metric :ton] [_ _ q _] (convert-str :brton :metricton q))

(defmethod convert [:imperial :us :cup] [_ _ q _] (convert-str :brcup :cup q))
(defmethod convert [:imperial :us :floz] [_ _ q _] (convert-str :brfloz :floz q))
(defmethod convert [:imperial :us :tablespoon] [_ _ q _] (convert-str :brtablespoon :tbsp q))
(defmethod convert [:imperial :us :teaspoon] [_ _ q _] (convert-str :brtsp :tsp q))
(defmethod convert [:imperial :us :gallon] [_ _ q _] (convert-str :brgallon :gallon q))
(defmethod convert [:imperial :us :pint] [_ _ q _] (convert-str :brpint :pint q))
(defmethod convert [:imperial :us :quart] [_ _ q _] (convert-str :brquart :quart q))
(defmethod convert [:imperial :us :gill] [_ _ q _] (convert-str :brgill :gill q))
(defmethod convert [:imperial :us :ton] [_ _ q _] (convert-str :brton :ton q))

;; Metric
(defmethod convert [:metric :us :l] [_ _ q _] (convert-str :liter :pint q))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str :dl :cup q))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str :cl :floz q))
(defmethod convert [:metric :us :ml] [_ _ q _] (convert-str :ml :tsp q))
(defmethod convert [:metric :us :km] [_ _ q _] (km->english q))
(defmethod convert [:metric :us :m] [_ _ q _] (m->english q))
(defmethod convert [:metric :us :dm] [_ _ q _] (dm->english q))
(defmethod convert [:metric :us :cm] [_ _ q _] (cm->english q))
(defmethod convert [:metric :us :mm] [_ _ q _] (mm->english q))
(defmethod convert [:metric :us :kg] [_ _ q _] (kg->english q))
(defmethod convert [:metric :us :hg] [_ _ q _] (hg->english q))
(defmethod convert [:metric :us :g] [_ _ q _] (g->english q))
(defmethod convert [:metric :us :mg] [_ _ q _] (mg->english q))
(defmethod convert [:metric :us :ton] [_ _ q _] (convert-str :metricton :ton q))

(defmethod convert [:metric :imperial :l] [_ _ q _] (convert-str :liter :brpint q))
(defmethod convert [:metric :imperial :dl] [_ _ q _] (convert-str :dl :brcup q))
(defmethod convert [:metric :imperial :cl] [_ _ q _] (convert-str :cl :brfloz q))
(defmethod convert [:metric :imperial :ml] [_ _ q _] (convert-str :ml :brtsp q))
(defmethod convert [:metric :imperial :km] [_ _ q _] (km->english q))
(defmethod convert [:metric :imperial :m] [_ _ q _] (m->english q))
(defmethod convert [:metric :imperial :dm] [_ _ q _] (dm->english q))
(defmethod convert [:metric :imperial :cm] [_ _ q _] (cm->english q))
(defmethod convert [:metric :imperial :mm] [_ _ q _] (mm->english q))
(defmethod convert [:metric :imperial :kg] [_ _ q _] (kg->english q))
(defmethod convert [:metric :imperial :hg] [_ _ q _] (hg->english q))
(defmethod convert [:metric :imperial :g] [_ _ q _] (g->english q))
(defmethod convert [:metric :imperial :mg] [_ _ q _] (mg->english q))
(defmethod convert [:metric :imperial :ton] [_ _ q _] (convert-str :metricton :brton q))

(defn convert-pounds-ounces [pounds-q _ oz-q _]
  (let [oz-in-pounds (convert-units :oz :pound oz-q)
        total-pounds (with-precision
                       (max (significant-digits pounds-q) (significant-digits oz-q) default-precision)
                       (+ pounds-q oz-in-pounds))]
    (convert :us :metric total-pounds :pound)))

(defn convert-feet-inches [feet-q _ inch-q _]
  (let [inches-in-feet (convert-units :inch :foot inch-q)
        total-feet (with-precision
                     (max (significant-digits feet-q) (significant-digits inch-q) default-precision)
                     (+ feet-q inches-in-feet))]
    (convert :us :metric total-feet :foot)))

(defn convert-interval [from-system to-system q1 dash q2 unit]
  (str (convert from-system to-system q1 unit) dash (convert from-system to-system q2 unit)))

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

