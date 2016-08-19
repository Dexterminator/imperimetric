(ns imperimetric.pages.home
  (:require [re-frame.core :refer [dispatch]]
            [imperimetric.utils-js :refer [target-value]]
            [imperimetric.components.button-areas :refer [from-button-area to-button-area]]
            [imperimetric.components.result-text :refer [result-text]]
            [imperimetric.components.text-entry :refer [text-entry]]))

(defn home-panel []
  (fn []
    [:div
     [:div#main-description
      "Welcome to "
      [:span#app-name-text "imperimetric"]
      ". Paste or type some text to automatically convert the measurements it contains into the system of measurement you prefer. "
      [:span#ounce-note
       "Note: imperimetric assumes 'ounces'/'oz' as weight ounces and 'fluid ounces'/'floz'/'fl. oz' as fluid ounces. "]
      [:div
       [:a#more-unit-info {:href "#/about"} "More unit info"]
       " "
       [:div.ounce-button {:class "tooltip-trigger" :on-click #(dispatch [:ounce-button-clicked])} "Make my ounces fluid!"
        [:span.tooltip {:id "ounce-tooltip"} "Converts 'ounce'/'oz' into 'fl. oz' if the text does not already contain any fluid ounces."]]]]
     [:div#button-area
      [from-button-area]
      [to-button-area]]
     [:div#convert-area
      [text-entry]
      [result-text]]]))
