(ns imperimetric.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.components.common :refer [header about result-text]]
            [imperimetric.js-utils :refer [target-value]]))

;; home
(defn home-panel []
  (fn []
    [:div
     [:div#main-description
      "Welcome to "
      [:span#app-name-text "Imperimetric"]
      ". Paste or type some text to automatically convert the measurments it contains into the system of measurement you prefer."]
     [:div#disclaimer (str "(Note: For the moment, only conversions from the metric system to US customary units for "
                           "fluid ounces and tablespoons is available. More units and options coming soon.)")]
     [:div#convert-area
      [:textarea {:placeholder "Paste or write some text here, like '1 ounce of water and 3 tablespoons of salt.'"
                  :on-change   #(dispatch [:text-changed (target-value %)])}]
      [result-text]]]))

;; about
(defn about-panel []
  (fn [] [about]))

;; main
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (fn []
      [:div
       [header]
       [:div#pagewrap
        [show-panel @active-panel]]])))
