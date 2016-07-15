(ns imperimetric.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [imperimetric.components.common :refer [header]]
            [imperimetric.js-utils :refer [target-value]]))

;; home
(defn home-panel []
  (fn []
    [:div
     [:div
      (str "Welcome to Imperimetric. Paste or type some text to automatically convert
        the measurments it contains into the system of measurement you desire.")]
     [:textarea {:on-change #(dispatch [:text-changed (target-value %)])}]]))

;; about
(defn about-panel []
  (fn []
    [:div "This is the About Page."]))

;; main
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
       [header]
       [:div#pagewrap
        [show-panel @active-panel]]])))
