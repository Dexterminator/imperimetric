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
   [:h2 [:a {:name "supported-units"}] "Supported units"]
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
                             {:unit "Milligrams" :symbols "(milligrams, mg)"}
                             {:unit "Tonnes" :symbols "(tons, tonnes)"}]]
    [unit-list-div "Area" [{:unit "Square kilometers" :symbols "(km², km^2, km2, square kilometers, sq km)"}
                           {:unit "Square meters" :symbols "(m², m^2, m2, square meters, sq m)"}
                           {:unit "Square decimeters" :symbols "(dm², dm^2, dm2, square decimeters, sq dm)"}
                           {:unit "Square centimeters" :symbols "(cm², cm^2, cm2, square centimeters, sq cm)"}
                           {:unit "Square millimeters" :symbols "(mm², mm^2, mm2, square millimeters, sq mm)"}
                           {:unit "Hectares" :symbols "(hectares, ha)"}]]
    [unit-list-div "Speed" [{:unit "Kilometers per hour" :symbols "(kilometers per hour, km/h, kmh, kph)"}
                            {:unit "Meters per second" :symbols "(meters per second, m/s)"}]]]
   [:h3.supported-unit-system "US and Imperial"]
   [:div.supported-units
    [unit-list-div "Volume" [{:unit "Cups" :symbols "(cups, cp)"}
                             {:unit "Fluid ounces" :symbols "(fluid ounces, fl. oz, floz)"}
                             {:unit "Tablespoon" :symbols "(tablespoons, tbsp)"}
                             {:unit "Teaspoon" :symbols "(teaspoons, tsp)"}
                             {:unit "Gallons" :symbols "(gallons, gal)"}
                             {:unit "Pints" :symbols "(pints, pt)"}
                             {:unit "Quarts" :symbols "(quarts, qt)"}
                             {:unit "Gills" :symbols "(gills)"}]]
    [unit-list-div "Distance" [{:unit "Miles" :symbols "(miles, mi)"}
                               {:unit "Yards" :symbols "(yards, yd)"}
                               {:unit "Feet" :symbols "(foot, feet, ft, ')"}
                               {:unit "Inches" :symbols "(inches, in, \")"}
                               {:unit "Combined" :symbols "(e.g 6 feet 4 inches, 6'4\")"}]]
    [unit-list-div "Weight" [{:unit "Pounds" :symbols "(pounds, lb)"}
                             {:unit "Ounces" :symbols "(ounces, oz)"}
                             {:unit "Tons" :symbols "(tons)"}
                             {:unit "Combined" :symbols "(e.g 6 pounds 2 ounces, 6lb 2oz)"}]]
    [unit-list-div "Area" [{:unit "Square miles" :symbols "(square miles, sq mi, mi², mi^2, mi2)"}
                           {:unit "Square yards" :symbols "(square yards, sq yd, yd², yd^2, yd2)"}
                           {:unit "Square feet" :symbols "(square feet, sq ft, ft², ft^2, ft2)"}
                           {:unit "Square inches" :symbols "(square inches, sq in, in², in^2, in2)"}
                           {:unit "Acres" :symbols "(acres, ac)"}]]
    [unit-list-div "Speed" [{:unit "Miles per hour" :symbols "(miles per hour, mph, mi/h)"}
                            {:unit "Feet per second" :symbols "(feet per second, ft/s, fps)"}]]]
   [:div "More units coming soon!"]])

(defn faq []
  [:div
   [:h2 "FAQ"]
   [:div.faq-question "Q: Why does the site look weird?"]
   [:div.faq-answer "A: This site uses some relatively new web stuff, try upgrading your browser if the site looks strange."]
   [:div.faq-question "Q: What determines the number of decimals after the decimal point in the conversions?"]
   [:div.faq-answer "A: The app uses a number of " [:a {:href "https://en.wikipedia.org/wiki/Significant_figures" :targer "_blank"}
                                                    "significant figures"]
    " for precision. By default, three significant figures are used in the result of a conversion. If the input number to a conversion"
    " has more significant figures than three, the input's number of significant figures is used in the result."]
   [:div.faq-question "Q: Why doesn't the button that converts ounces to fluid ounces do anything? I have ounces in my text."]
   [:div.faq-answer "A: If the text contains both ounces and fluid ounces, the app assumes that this is because the input text "
    "disambiguates the different types of ounces, meaning that 'ounce' signifies a weight ounce with relatively high probability and should "
    "not be changed into a fluid ounce."]
   [:div.faq-question "Q: Why arent tons/tonnes behaving as I expect?"]
   [:div.faq-answer "A: " [:a {:href "https://en.wikipedia.org/wiki/Ton" :target "_blank"} "Tons/tonnes"]
    " are used differently in different countries, but some uses seem to be more common. "
    "I use the same conversions as Wikipedia and Google, where an Imperial ton is 2.240 lb, a US ton is 2000 lb and a metric ton "
    "is 1000 kg."]
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
