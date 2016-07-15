(ns imperimetric.js-utils)

(defn js-apply [f target args]
  (.apply f target (to-array args)))

(defn log [& args]
  (js-apply (.-log js/console) js/console args))

(defn target-value [event]
  (-> event .-target .-value))
