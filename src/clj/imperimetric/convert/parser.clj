(ns imperimetric.convert.parser
  (:require [instaparse.core :as insta]))

(def base-grammar "src/clj/imperimetric/grammars/base-grammar.bnf")
(def metric-grammar "src/clj/imperimetric/grammars/metric-grammar.bnf")
(def english-metric-grammar "src/clj/imperimetric/grammars/english-metric-grammar.bnf")
(def english-english-grammar "src/clj/imperimetric/grammars/english-english-grammar.bnf")

(defn make-parser [grammar-file]
  (insta/parser (str (slurp base-grammar) (slurp grammar-file))))

(defn get-parser [from to]
  (cond
    (= from :metric) (make-parser metric-grammar)
    (= to :metric) (make-parser english-metric-grammar)
    :else (make-parser english-english-grammar)))

(defn parse-text [text from-system to-system]
  ((get-parser from-system to-system) text))
