(ns imperimetric.utils-js
  (:require
    [goog.userAgent.product :refer [CHROME FIREFOX SAFARI IE EDGE OPERA isVersion]]
    goog.userAgent.product.isVersion))

(defn js-apply [f target args]
  (.apply f target (to-array args)))

(defn log [& args]
  (js-apply (.-log js/console) js/console args))

(defn target-value [event]
  (-> event .-target .-value))

(defn copy-supported []
  (or
    (and CHROME (isVersion 49))
    (and FIREFOX (isVersion 47))
    (and OPERA (isVersion 39))))

(defn select-supported []
  (and SAFARI (isVersion 9)))

(defn copy-message []
  (cond
    (copy-supported) "Copied!"
    (select-supported) "Press ⌘-C to copy"
    :else "Not supported :("))
