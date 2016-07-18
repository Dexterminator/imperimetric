(ns imperimetric.components.common
  (:require [re-frame.core :refer [subscribe]]))

(defn header []
  [:header#header
   [:div#main-nav
    [:a#app-nav-name {:href "#/"} "Imperimetric"]
    [:a#about-nav {:href "#/about"} "What is this?"]]])

(defn about []
  [:div (str "Remembering how to convert between systems of measurements is tedious. "
             "Imperimetric is an attempt to avoid at least some of the tediousness.")
   [:h5 "More:"]
   [:ul
    [:li [:a {:href "https://en.wikipedia.org/wiki/System_of_measurement" :target "_blank"}
          "System of measurement"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Metric_system" :target "_blank"}
          "Metric system"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/United_States_customary_unitsl" :target "_blank"}
          "US customary units"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Imperial_units" :target "_blank"}
          "Imperial units"]]]])

(defn result-text []
  (let [text (subscribe [:converted-text])]
    (fn []
      [:div#result-text @text])))
