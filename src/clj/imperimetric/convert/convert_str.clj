(ns imperimetric.convert.convert-str
  (:require [imperimetric.convert.precision :refer [significant-digits default-precision]]
            [frinj.ops :refer [fj to]]))

(def unit->suffix
  {:liter  "l" :dl "dl" :cl "cl" :ml "ml" :km "km" :meter "m" :dm "dm" :cm "cm" :mm "mm" :kg "kg" :hg "hg" :g "g" :mg "mg"
   :floz   "fl. oz" :brfloz "fl. oz" :cup "cups" :brcup "cups" :tbsp "tbsp" :brtablespoon "tbsp" :tsp "tsp" :brtsp "tsp"
   :gallon "gallons" :brgallon "gallons" :pint "pints" :brpint "pints" :quart "quarts" :brquart "quarts"
   :mile   "miles" :yard "yards" :foot "feet" :inch "inches" :pound "pounds" :oz "oz" :gill "gills" :brgill "gills"
   :ton    "tons" :metricton "tonnes" :brton "tons" :km2 "km²" :m2 "m²" :dm2 "dm²" :cm2 "cm²", :mm2 "mm²"
   :sqmile "sq mi" :sqyard "sq yd" :sqfoot "sq ft" :sqinch "sq in" :hectare "hectares" :acre "acres" :kph "km/h"
   :mph    "mph" :fps "ft/s" :mps "m/s" :celsius "°C" :fahrenheit "°F"})

(def s-suffix-units #{:cup :brcup :gallon :brgallon :pint :brpint :quart :brquart :mile :yard :hectare :acre})

(defn- singular [unit]
  (cond
    (s-suffix-units unit) (let [suffix (unit->suffix unit)]
                            (subs suffix 0 (dec (count suffix))))
    (= :foot unit) "foot"
    :else (unit->suffix unit)))

(defn celsius->fahrenheit [q]
  (bigdec (+ 32 (* q 9/5))))

(defn fahrenheit->celsius [q]
  (bigdec (* 5/9 (- q 32))))

(defn convert-units [from-unit to-unit q]
  (let [qr (rationalize q)
        digits (significant-digits qr)
        precision (if (and (not (ratio? q)) (> digits default-precision)) digits default-precision)]
    (with-precision precision
      (case from-unit
        :celsius (celsius->fahrenheit qr)
        :fahrenheit (fahrenheit->celsius qr)
        (-> (fj qr from-unit) (to to-unit) :v bigdec)))))

(defn convert-str [from to quantity]
  (let [converted-quantity (convert-units from to quantity)
        suffix (if (= 1M converted-quantity)
                 (singular to)
                 (unit->suffix to))]
    (str (.toPlainString converted-quantity) (if-not (#{:celsius :fahrenheit} from) " ") suffix)))
