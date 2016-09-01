(ns imperimetric.components.text-entry
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.utils-js :refer [target-value]]))

(def placeholder
  (str "Paste or write some text here, like \"3 cups of milk and 1/4 pound butter\" or \"My 450 sq ft apartment is 2 miles"
  " away\", or \"the engine temperature exceeded 250°F as the car reached 110 mph\"."))

(defn text-entry []
  (let [text (subscribe [:text])]
    (fn []
      [:textarea#text-entry {:max-length  "2800"
                             :placeholder placeholder
                             :on-change   #(dispatch [:text-changed (target-value %)])
                             :value       @text}])))
