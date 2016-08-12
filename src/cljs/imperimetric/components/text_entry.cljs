(ns imperimetric.components.text-entry
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.utils-js :refer [target-value]]))

(defn text-entry []
  (let [text (subscribe [:text])]
    (fn []
      [:textarea#text-entry {:max-length  "2800"
                             :placeholder "Paste or write some text here, like '3 cups of milk and 2 tablespoons of sugar.'"
                             :on-change   #(dispatch [:text-changed (target-value %)])
                             :value       @text}])))
