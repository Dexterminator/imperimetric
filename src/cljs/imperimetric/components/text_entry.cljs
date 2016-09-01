(ns imperimetric.components.text-entry
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.utils-js :refer [target-value]]))

(def english-placeholder
  (str "Paste or write some text here, like \n\"3 cups of milk and 1/4 pound butter\" or \n\"My 450 sq ft apartment is 2 miles"
  " away\" or \n\"the engine temperature exceeded 250°F as the car reached 110 mph\"."))

(def metric-placeholder
  (str "Paste or write some text here, like \n\"700 ml of milk and 100 grams butter\" or \n\"My 40 m² apartment is 3 kilometers"
       " away\" or \n\"the engine temperature exceeded 120°C as the car reached 180 km/h\"."))

(defn text-entry []
  (let [text (subscribe [:text])
        from-system (subscribe [:from-system])]
    (fn []
      [:textarea#text-entry {:max-length  "2800"
                             :placeholder (if (= :metric @from-system)
                                            metric-placeholder
                                            english-placeholder)
                             :on-change   #(dispatch [:text-changed (target-value %)])
                             :value       @text}])))
