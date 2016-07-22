(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]))

(def parsers
  {:metric (insta/parser "src/clj/imperimetric/metric-grammar.bnf")
   :us     (insta/parser "src/clj/imperimetric/us-grammar.bnf")})

(defn parse-recipe [recipe from-system]
  ((parsers from-system) recipe))

(defn decimal-round [n]
  (format "%.1f" n))

(def cup-l-ratio 0.236588)
(def cup-dl-ratio 2.36588)
(def oz-cl-ratio 2.95735)
(def tbsp-ml-ratio 14.7868)
(def tsp-ml-ratio 4.92892)

(defn cup->dl [q] (* q cup-dl-ratio))
(defn oz->cl [q] (* q oz-cl-ratio))
(defn tbsp->ml [q] (* q tbsp-ml-ratio))
(defn tsp->ml [q] (* q tsp-ml-ratio))

(defn l->cup [q] (/ q cup-l-ratio))
(defn dl->cup [q] (/ q cup-dl-ratio))
(defn cl->oz [q] (/ q oz-cl-ratio))
(defn ml->tsp [q] (/ q tsp-ml-ratio))

(defn convert-str [q suffix]
  (str (decimal-round q) " " suffix))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str (cup->dl q) "dl"))
(defmethod convert [:us :metric :oz] [_ _ q _] (convert-str (oz->cl q) "cl"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str (tbsp->ml q) "ml"))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str (tsp->ml q) "ml"))

(defmethod convert [:metric :us :l] [_ _ q _] (convert-str (l->cup q) "cup"))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str (dl->cup q) "cup"))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str (cl->oz q) "oz"))
(defmethod convert [:metric :us :ml] [_ _ q _] (convert-str (ml->tsp q) "tsp"))

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
