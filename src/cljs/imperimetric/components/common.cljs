(ns imperimetric.components.common
  (:require [re-frame.core :refer [subscribe]]
            [clojure.string :as str]))

(defn header [active-panel]
  [:header#header
   [:div#main-nav
    [:a#app-nav-name {:href "#/"} "Imperimetric"]
    (if (= active-panel :about-panel)
      [:a#about-nav {:href "#/"} "Back"]
      [:a#about-nav {:href "#/about"} "What is this?"])]])

(defn about []
  [:div
   (str "Today, the metric system is used in a majority of the world's countries. "
        "However, US customary units and the Imperial system still prevail, and people from countries "
        "that use different systems often encounter recipes in systems they don't know by heart. "
        "Remembering how to convert between systems of measurements is tedious. "
        "Instead of you having to convert from cups to deciliters or the other way around, ")
   [:span#app-name-text "Imperimetric"]
   (str " aims to convert the whole text automatically. For example, \"3 cups milk "
        "and 2 tablespoons sugar\" becomes \"7.1 dl milk and 29.6 ml sugar\".")
   [:h5 "More info:"]
   [:ul
    [:li [:a {:href "https://en.wikipedia.org/wiki/System_of_measurement" :target "_blank"}
          "System of measurement"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Metric_system" :target "_blank"}
          "Metric system"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/United_States_customary_units" :target "_blank"}
          "US customary units"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Imperial_units" :target "_blank"}
          "Imperial units"]]]])

(js/Clipboard. "#copy")

(defn result-text []
  (let [text (subscribe [:converted-text])]
    (fn []
      [:div#result-text
       [:div.tooltip-trigger {:id "copy" :data-clipboard-text @text :class (if (str/blank? @text) "hidden")}
        [:img#clipboard {:src "images/clipboard.svg"}]
        [:span.tooltip "Copy to clipboard"]]
       @text])))
