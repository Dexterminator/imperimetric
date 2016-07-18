(ns imperimetric.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [imperimetric.convert :refer [convert-recipe]]
            [medley.core :refer [map-keys]]))

(def param-max-length 2000)

(defn params [ctx]
  (map-keys keyword (get-in ctx [:request :params])))

(defresource conversion
  :available-media-types ["text/plain"]
  :uri-too-long? (fn [ctx] (< param-max-length (count (get-in ctx [:request :query-string]))))
  :handle-ok (fn [ctx]
               (println ctx)
               (let [{text :text
                      from :from
                      to   :to} (params ctx)]
                 (convert-recipe text (keyword from) (keyword to)))))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/convert" [] conversion)
  (resources "/"))

(def dev-handler (-> #'routes wrap-reload wrap-trace wrap-params))

(def handler (-> routes wrap-params))
