(ns imperimetric.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [imperimetric.components.common :refer [header about result-text]]
            [cljsjs.clipboard]
            [imperimetric.utils-js :refer [target-value]]))

(defn from-button [text system active-from-system]
  [:div {:on-click #(dispatch [:from-button-clicked system])
         :class    (str "button " (if (= @active-from-system system) "active-button"))}
   text])

(defn from-button-area []
  (let [from-system (subscribe [:from-system])]
    (fn []
      [:div#left-button-group
       [:div.button-group-headline "From"]
       [from-button "US customary units" :us from-system]
       [from-button "Imperial" :imperial from-system]
       [from-button "Metric" :metric from-system]])))

(defn to-button [text system active-to-system]
  [:div {:on-click #(dispatch [:to-button-clicked system])
         :class    (str "button " (if (= @active-to-system system) "active-button"))}
   text])

(defn to-button-area []
  (let [to-system (subscribe [:to-system])]
    (fn []
      [:div#right-button-group
       [:div.to-button-group-headline "To"]
       [to-button "US customary units" :us to-system]
       [to-button "Imperial" :imperial to-system]
       [to-button "Metric" :metric to-system]])))

;; home
(defn home-panel []
  (fn []
    [:div
     [:div#main-description
      "Welcome to "
      [:span#app-name-text "imperimetric"]
      ". Paste or type some text to automatically convert the measurements it contains into the system of measurement you prefer."]
     [:div#button-area
      [from-button-area]
      [to-button-area]]
     [:div#convert-area
      [:textarea#text-entry {:max-length "1200"
                             :placeholder "Paste or write some text here, like '3 cups of milk and 2 tablespoons of sugar.'"
                             :on-change   #(dispatch [:text-changed (target-value %)])}]
      [result-text]]]))

;; about
(defn about-panel []
  (fn [] [about]))

;; main
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])
        loading (subscribe [:loading])]
    (fn []
      [:div
       [header @active-panel @loading]
       [:div#pagewrap
        [show-panel @active-panel]]])))
