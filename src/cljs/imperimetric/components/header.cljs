(ns imperimetric.components.header)

(defn header [active-panel]
  [:header#header
   [:div#main-nav
    [:a#app-nav-name {:href "#/"} "imperimetric"]
    (if (= active-panel :about-panel)
      [:a#about-nav {:href "#/"} "Back"]
      [:a#about-nav {:href "#/about"} "What is this?"])]])
