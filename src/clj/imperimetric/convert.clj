(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]
            [frinj.jvm :refer [frinj-init!]]
            [frinj.ops :refer [fj]]))

(frinj-init!)

(def parsers
  {:metric (insta/parser "src/clj/imperimetric/metric-grammar.bnf")
   :us     (insta/parser "src/clj/imperimetric/us-grammar.bnf")})

(defn parse-recipe [recipe from-system]
  ((parsers from-system) recipe))

(defn decimal-round [n]
  (format "%.1f" n))

(defn convert-str [q suffix]
  (str (decimal-round q) " " suffix))

(defn convert-units [from to q]
  (double (:v (fj q from :to to))))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str (convert-units :cup :dl q) "dl"))
(defmethod convert [:us :metric :oz] [_ _ q _] (convert-str (convert-units :floz :cl q) "cl"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str (convert-units :tbsp :ml q) "ml"))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str (convert-units :tsp :ml q) "ml"))

(defmethod convert [:metric :us :l] [_ _ q _] (convert-str (convert-units :liter :cup q) "cup"))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str (convert-units :dl :cup q) "cup"))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str (convert-units :cl :floz q) "oz"))
(defmethod convert [:metric :us :ml] [_ _ q _] (convert-str (convert-units :ml :tsp q) "tsp"))

(defn transform-map [from-system to-system]
  (merge
    (map-all-to [:recipe :token :word :whitespace] str)
    (map-all-to [:integer :fraction :decimal] read-string)
    {:measurement (partial convert from-system to-system)
     :quantity    identity
     :unit        first
     :mixed       +}))

(defn convert-recipe [recipe from-system to-system]
  (let [parsed (parse-recipe recipe from-system)]
    (if (insta/failure? parsed)
      nil
      (str/join (insta/transform (transform-map from-system to-system) parsed)))))
