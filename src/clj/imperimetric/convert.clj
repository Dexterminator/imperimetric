(ns imperimetric.convert
  (:require [instaparse.core :as insta]
            [clojure.string :as str]
            [imperimetric.util :refer [map-all-to]]))

(def parse-recipe
  (insta/parser "src/clj/imperimetric/recipe-grammar.bnf"))

(def oz-cl-ratio 2.95735)
(def tbsp-cl-ratio 1.47868)

(defn decimal-round [n]
  (format "%.2f" n))

(defn oz->cl [q]
  (* q oz-cl-ratio))

(defn cl->oz [q]
  (/ q oz-cl-ratio))

(defn tbsp->cl [q]
  (* q tbsp-cl-ratio))

(defmulti convert
  (fn [from-system to-system quantity unit] [from-system to-system unit]))

(defmethod convert [:us :metric :oz] [_ _ q _] (str (decimal-round (oz->cl q)) " cl"))
(defmethod convert [:metric :us :cl] [_ _ q _] (str (decimal-round (cl->oz q)) " oz"))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (str (decimal-round (tbsp->cl q)) " cl"))

(defn convert-recipe [recipe from-system to-system]
  (str/join
    (insta/transform
      (merge
        (map-all-to [:recipe :token :word :whitespace] str)
        (map-all-to [:integer :fraction :decimal] read-string)
        {:measurement (partial convert from-system to-system)
         :quantity    identity
         :unit        first
         :mixed       +})
      (parse-recipe recipe))))
