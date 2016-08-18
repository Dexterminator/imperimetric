(ns imperimetric.pages.about)

(defn motivation []
  [:div
   [:h2 "Motivation"]
   [:p
    "Today, the "
    [:a {:href "https://en.wikipedia.org/wiki/Metric_system" :target "_blank"} "metric system"]
    " is used in a majority of the world's countries. "
    "However, "
    [:a {:href "https://en.wikipedia.org/wiki/United_States_customary_units" :target "_blank"} "US customary units"]
     " and the "
    [:a {:href "https://en.wikipedia.org/wiki/Imperial_units" :target "_blank"} "Imperial system"]
     " still prevail, and people from countries "
    "that use different systems often encounter recipes and other text in systems they don't know by heart. "]
   [:p
     "Remembering how to convert between "
    [:a {:href "https://en.wikipedia.org/wiki/System_of_measurement" :target "_blank"} "systems of measurement"]
     " is tedious. "
    "Instead of you having to convert from cups to deciliters or the other way around, "
    [:span#app-name-text "imperimetric"]
    (str " aims to convert the whole text automatically. For example, \"2 cups milk "
         "and 4 tablespoons sugar\" becomes \"4.73 dl milk and 59.1 ml sugar\".")]])

(defn unit-list-div [title units]
  [:div.unit-list
   [:h4.supported-unit-type title]
   [:ul
    (for [unit units]
      ^{:key (:unit unit)} [:li (:unit unit) [:div.unit-symbols (:symbols unit)]])]])

(defn supported-units []
  [:div
   [:h2 "Supported units"]
   [:h3.supported-unit-system "Metric"]
   [:div.supported-units
    [unit-list-div "Volume" [{:unit "Liters" :symbols "(liters, litres, l)"}
                             {:unit "Deciliters" :symbols "(deciliters, decilitres, dl)"}
                             {:unit "Centiliters" :symbols "(centiliters, centilitres, cl)"}
                             {:unit "Milliliters" :symbols "(milliliters, millilitres, ml)"}]]
    [unit-list-div "Distance" [{:unit "Kilometers" :symbols "(kilometers, kilometres, km)"}
                               {:unit "Meters" :symbols "(meters, metres, m)"}
                               {:unit "Decimeters" :symbols "(decimeters, decimetres, dm)"}
                               {:unit "Centimeters" :symbols "(centimeters, centimetres, cm)"}
                               {:unit "Millimeters" :symbols "(millimeters, millimetres, mm)"}]]
    [unit-list-div "Weight" [{:unit "Kilograms" :symbols "(kilograms, kg)"}
                             {:unit "Hectograms" :symbols "(hectograms, hg)"}
                             {:unit "Grams" :symbols "(grams, g)"}
                             {:unit "Milligrams" :symbols "(milligrams, mg)"}]]]
   [:h3.supported-unit-system "US and Imperial"]
   [:div.supported-units
    [unit-list-div "Volume" [{:unit "Cups" :symbols "(cups, cp)"}
                             {:unit "Fluid ounces" :symbols "(fluid ounces, fl. oz, floz)"}
                             {:unit "Tablespoon" :symbols "(tablespoons, tbsp)"}
                             {:unit "Teaspoon" :symbols "(teaspoons, tsp)"}
                             {:unit "Gallons" :symbols "(gallons, gal)"}
                             {:unit "Pints" :symbols "(pints, pt)"}
                             {:unit "Quarts" :symbols "(quarts, qt)"}]]
    [unit-list-div "Distance" [{:unit "Miles" :symbols "(miles, mi)"}
                               {:unit "Yards" :symbols "(yards, yd)"}
                               {:unit "Feet" :symbols "(foot, feet, ft, ')"}
                               {:unit "Inches" :symbols "(inches, in, \")"}]]
    [unit-list-div "Weight" [{:unit "Pounds" :symbols "(pounds, lb)"}
                             {:unit "Ounces" :symbols "(ounces, oz)"}]]]])

(defn faq []
  [:div
   [:h2 "FAQ"]
   [:div.faq-question "Q: Why does the site look weird?"]
   [:div.faq-answer "A: This site uses some relatively new web stuff, try upgrading your browser if the site looks strange."]
   [:div.faq-question "Q: I have a suggestion/I found a bug! Who do I talk to?"]
   [:div.faq-answer
    "A: You can contact " [:a {:href "http://www.dxtr.se" :target "_blank"} "me"] " by email right "
    [:a {:href "mailto:dexter.gramfors@gmail.com?Subject=imperimetric" :target "_blank"} "here"] "."]
   [:div.faq-question "Q: Can I see the source code anywhere?"]
   [:div.faq-answer "A: Yes, the code is on " [:a {:href "https://github.com/Dexterminator/imperimetric" :target "_blank"} "GitHub"] "."]])

(defn about []
  [:div
   [motivation]
   [supported-units]
   [faq]])

(defn about-panel []
  (fn [] [about]))
