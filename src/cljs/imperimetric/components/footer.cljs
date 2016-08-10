(ns imperimetric.components.footer)

(defn footer []
  [:div
   [:footer#footer "By " [:span [:a {:href "http://www.dxtr.se/" :target "_blank"} "Dexter Gramfors"]]]])
