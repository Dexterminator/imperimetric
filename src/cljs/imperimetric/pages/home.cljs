(ns imperimetric.pages.home
  (:require [re-frame.core :refer [dispatch]]
            [imperimetric.utils-js :refer [target-value]]
            [imperimetric.components.button-areas :refer [from-button-area to-button-area]]
            [imperimetric.components.result-text :refer [result-text]]))

(defn home-panel []
  (fn []
    [:div
     [:div#main-description
      "Welcome to "
      [:span#app-name-text "imperimetric"]
      ". Paste or type some text to automatically convert the measurements it contains into the system of measurement you prefer."]
     [:div#button-area
      [from-button-area]
      [to-button-area]]
     [:div#convert-area
      [:textarea#text-entry {:max-length  "4800"
                             :placeholder "Paste or write some text here, like '3 cups of milk and 2 tablespoons of sugar.'"
                             :on-change   #(dispatch [:text-changed (target-value %)])}]
      [result-text]]]))
