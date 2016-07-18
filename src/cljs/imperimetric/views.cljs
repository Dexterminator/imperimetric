(ns imperimetric.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.components.common :refer [header]]
            [imperimetric.js-utils :refer [target-value]]))

(defn result-text []
  (let [text (subscribe [:converted-text])]
    (fn []
      [:div#result-text @text])))

;; home
(defn home-panel []
  (fn []
    [:div
     [:div#main-description
      (str "Welcome to Imperimetric. Paste or type some text to automatically convert
        the measurments it contains into the system of measurement you desire."
           " (Note: For the moment, only metric to US customary units for fluid ounces and tablespoons is available."
           " More units and options coming soon.")]
     [:div#convert-area
      [:textarea {:on-change #(dispatch [:text-changed (target-value %)])}]
      [result-text]]]))

;; about
(defn about-panel []
  (fn []
    [:div (str "Remembering how to convert between systems of measurements is tedious. "
               "This page is an attempt to avoid at least some of the tediousness.")
     [:h5 "More:"]
     [:ul
      [:li [:a {:href "https://en.wikipedia.org/wiki/System_of_measurement" :target "_blank"}
            "System of measurement"]]
      [:li [:a {:href "https://en.wikipedia.org/wiki/Metric_system" :target "_blank"}
            "Metric system"]]
      [:li [:a {:href "https://en.wikipedia.org/wiki/United_States_customary_unitsl" :target "_blank"}
            "US customary units"]]
      [:li [:a {:href "https://en.wikipedia.org/wiki/Imperial_units" :target "_blank"}
            "Imperial units"]]]]))

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
