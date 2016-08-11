(ns imperimetric.pages.about)

(defn string-ul [& items]
  [:ul
   (for [item items]
     ^{:key item} [:li item])])

(defn motivation []
  [:div
   [:h2 "Motivation"]
   [:p
    (str "Today, the metric system is used in a majority of the world's countries. "
         "However, US customary units and the Imperial system still prevail, and people from countries "
         "that use different systems often encounter recipes and other text in systems they don't know by heart. ")]
   [:p
    (str "Remembering how to convert between systems of measurements is tedious. "
         "Instead of you having to convert from cups to deciliters or the other way around, ")
    [:span#app-name-text "imperimetric"]
    (str " aims to convert the whole text automatically. For example, \"3 cups milk "
         "and 2 tablespoons sugar\" becomes \"7.1 dl milk and 29.6 ml sugar\".")]])

(defn unit-list-div [title units]
  [:div.unit-list
   [:h4.supported-unit-type title]
   (apply string-ul units)])

(defn supported-units []
  [:div
   [:h2 "Supported units"]
   [:h3.supported-unit-system "Metric"]
   [:div.supported-units
    [unit-list-div "Volume" ["Liters" "Deciliters" "Centiliters" "Milliliters"]]
    [unit-list-div "Distance" ["Kilometers" "Meters" "Decimeters" "Millimeters"]]
    [unit-list-div "Weight" ["Kilograms" "Hectograms" "Grams" "Milligrams"]]]
   [:h3.supported-unit-system "US and Imperial"]
   [:div.supported-units
    [unit-list-div "Volume" ["Cups" "Ounces (fluid)" "Tablespoons" "Teaspoons" "Gallons" "Pints" "Quarts"]]
    [unit-list-div "Distance" ["Miles" "Yards" "Feet" "Inches"]]
    [unit-list-div "Weight" ["Pounds" "Ounces (dry)"]]]])

(defn more-info []
  [:div
   [:h2 "More info"]
   [:ul
    [:li [:a {:href "https://en.wikipedia.org/wiki/System_of_measurement" :target "_blank"}
          "System of measurement"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Metric_system" :target "_blank"}
          "Metric system"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/United_States_customary_units" :target "_blank"}
          "US customary units"]]
    [:li [:a {:href "https://en.wikipedia.org/wiki/Imperial_units" :target "_blank"}
          "Imperial units"]]]])

(defn about []
  [:div
   [motivation]
   [supported-units]
   [more-info]])

(defn about-panel []
  (fn [] [about]))
