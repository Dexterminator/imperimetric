(ns imperimetric.convert.precision)

(def default-precision 3)

(defn significant-digits [n]
  (let [stripped (.stripTrailingZeros (with-precision 100 (bigdec n)))
        precision (.precision stripped)
        scale (.scale stripped)]
    (if (neg? scale)
      (- precision scale)
      precision)))
