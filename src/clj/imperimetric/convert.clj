(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]))

(def parse-recipe
  (insta/parser "src/clj/imperimetric/recipe-grammar.bnf"))

(def oz-cl-ratio 2.95735)
(def tbsp-cl-ratio 1.47868)
(def cup-cl-ratio 23.6588)

(defn decimal-round [n]
  (format "%.1f" n))

(defn oz->cl [q]
  (* q oz-cl-ratio))

(defn cl->oz [q]
  (/ q oz-cl-ratio))

(defn tbsp->cl [q]
  (* q tbsp-cl-ratio))

(defn cup->dl [q]
  (* q cup-cl-ratio 0.1))

(defn cl->cup [q]
  (/ q cup-cl-ratio))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

(defmethod convert [:us :metric :oz] [_ _ q _] (str (decimal-round (oz->cl q)) " cl"))
(defmethod convert [:metric :us :cl] [_ _ q _] (str (decimal-round (cl->oz q)) " oz"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (str (decimal-round (tbsp->cl q)) " cl"))
(defmethod convert [:us :metric :cup] [_ _ q _] (str (decimal-round (cup->dl q)) " dl"))

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
      (str/join (insta/transform
                  (transform-map from-system to-system) parsed)))))
