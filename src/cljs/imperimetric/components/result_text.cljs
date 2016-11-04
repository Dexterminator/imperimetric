(ns imperimetric.components.result-text
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe]]
            [clojure.string :as str]
            [imperimetric.utils-js :refer [copy-message]]
            [cljsjs.clipboard]))

(js/Clipboard. "#copy")

(defn result-text [loading?]
  (let [text (subscribe [:converted-text])
        loading? (subscribe [:loading?])
        default-tooltip "Copy to clipboard"
        tooltip-text (r/atom default-tooltip)]
    (fn []
      [:div#result-text
       [:div.tooltip-trigger {:id                  "copy"
                              :data-clipboard-text @text
                              :class               (if (str/blank? @text) "hidden")
                              :on-click            #(reset! tooltip-text (copy-message))
                              :on-mouse-leave      #(reset! tooltip-text default-tooltip)}
        [:img#clipboard {:src "images/clipboard.svg"}]
        [:span.tooltip @tooltip-text]]
       [:span {:class (if @loading? "result-text-loading")} @text]
       (if @loading? [:div.spinner])])))
