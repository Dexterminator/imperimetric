(ns imperimetric.components.common)

(defn header []
  [:header#header
   [:div#main-nav
    [:a#app-nav-name {:href "#/"} "Imperimetric"]
    [:a#about-nav {:href "#/about"} "Why?"]]])
