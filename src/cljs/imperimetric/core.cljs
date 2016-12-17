(ns imperimetric.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as rf]
              [devtools.core :as devtools]
              [imperimetric.handlers]
              [imperimetric.subs]
              [imperimetric.routes :as routes]
              [imperimetric.views :as views]
              [imperimetric.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
