(ns imperimetric.precision)

(def default-precision 3)

(defn significant-digits [n]
  (let [stripped (.stripTrailingZeros (with-precision 100 (bigdec n)))
        precision (.precision stripped)
        scale (.scale stripped)]
    (if (< scale 0)
      (- precision scale)
      precision)))
