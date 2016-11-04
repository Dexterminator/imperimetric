(ns imperimetric.views
  (:require [imperimetric.pages.home :refer [home-panel]]
            [imperimetric.pages.about :refer [about-panel]]
            [imperimetric.components.header :refer [header]]
            [imperimetric.components.footer :refer [footer]]
            [re-frame.core :refer [subscribe]]))

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
       [header @active-panel]
       [:div#pagewrap
        [show-panel @active-panel]
        [footer]]])))
