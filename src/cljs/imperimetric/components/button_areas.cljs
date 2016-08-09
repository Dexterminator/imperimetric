(ns imperimetric.components.button-areas
  (:require [re-frame.core :refer [dispatch subscribe]]))

(defn from-button [text system active-from-system]
  [:div {:on-click #(dispatch [:from-button-clicked system])
         :class    (str "button " (if (= @active-from-system system) "active-button"))}
   text])

(defn to-button [text system active-to-system]
  [:div {:on-click #(dispatch [:to-button-clicked system])
         :class    (str "button " (if (= @active-to-system system) "active-button"))}
   text])

(defn from-button-area []
  (let [from-system (subscribe [:from-system])]
    (fn []
      [:div#left-button-group
       [:div.button-group-headline "From"]
       [from-button "US customary units" :us from-system]
       [from-button "Imperial" :imperial from-system]
       [from-button "Metric" :metric from-system]])))

(defn to-button-area []
  (let [to-system (subscribe [:to-system])]
    (fn []
      [:div#right-button-group
       [:div.to-button-group-headline "To"]
       [to-button "US customary units" :us to-system]
       [to-button "Imperial" :imperial to-system]
       [to-button "Metric" :metric to-system]])))

