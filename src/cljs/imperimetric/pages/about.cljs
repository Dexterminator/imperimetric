(ns imperimetric.pages.about)

(defn about-panel []
  (fn [] [about]))

(defn about []
  [:div
   (str "Today, the metric system is used in a majority of the world's countries. "
        "However, US customary units and the Imperial system still prevail, and people from countries "
        "that use different systems often encounter recipes in systems they don't know by heart. "
        "Remembering how to convert between systems of measurements is tedious. "
        "Instead of you having to convert from cups to deciliters or the other way around, ")
   [:span#app-name-text "imperimetric"]
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
