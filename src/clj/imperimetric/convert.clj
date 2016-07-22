(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]))

(def parse-recipe
  (insta/parser "src/clj/imperimetric/recipe-grammar.bnf"))

(def oz-cl-ratio 2.95735)
(def tbsp-ml-ratio 14.7868)
(def cup-dl-ratio 2.36588)
(def tsp-ml-ratio 4.92892)

(defn decimal-round [n]
  (format "%.1f" n))

(defn oz->cl [q]
  (* q oz-cl-ratio))

(defn cl->oz [q]
  (/ q oz-cl-ratio))

(defn cup->dl [q]
  (* q cup-dl-ratio))

(defn tbsp->ml [q]
  (* q tbsp-ml-ratio))

(defn tsp->ml [q]
  (* q tsp-ml-ratio))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

(defmethod convert [:us :metric :oz] [_ _ q _] (str (decimal-round (oz->cl q)) " cl"))
(defmethod convert [:metric :us :cl] [_ _ q _] (str (decimal-round (cl->oz q)) " oz"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (str (decimal-round (tbsp->ml q)) " ml"))
(defmethod convert [:us :metric :cup] [_ _ q _] (str (decimal-round (cup->dl q)) " dl"))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (str (decimal-round (tsp->ml q)) " ml"))

(defn transform-map [from-system to-system]
  (merge
    (map-all-to [:recipe :token :word :whitespace] str)
    (map-all-to [:integer :fraction :decimal] read-string)
    {:measurement (partial convert from-system to-system)
     :quantity    identity
     :unit        first
     :mixed       +}))

(defn convert-recipe [recipe from-system to-system]
  (let [parsed (parse-recipe recipe)]
    (if (insta/failure? parsed)
      nil
      (str/join (insta/transform (transform-map from-system to-system) parsed)))))
