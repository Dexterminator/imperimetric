(ns imperimetric.util)

(defn map-all-to [from-coll to]
  (into {} (map #(vector % to) from-coll)))
