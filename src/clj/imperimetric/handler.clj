(ns imperimetric.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [resource-response]]
            [ring.util.codec :refer [url-decode url-encode]]
            [ring.middleware.reload :refer [wrap-reload]]
            [imperimetric.convert.core :refer [convert-text]]
            [medley.core :refer [map-keys]]
            [cheshire.core :as json]))

(def param-max-length 3000)

(defn params [ctx]
  (map-keys keyword (get-in ctx [:request :params])))

(def systems #{"us" "imperial" "metric"})

(defresource conversion
  :available-media-types ["application/json"]
  :uri-too-long? (fn [ctx]
                   (< param-max-length
                      (count (url-decode (get-in ctx [:request :query-string])))))
  :malformed? (fn [ctx]
                (let [{from :from to :to} (params ctx)]
                  (if-not (and (systems from) (systems to))
                    {:message "Bad request: Allowed systems are: \"us\", \"imperial\", and \"metric\"."})))
  :handle-ok (fn [ctx]
               (let [{text :text
                      from :from
                      to   :to} (params ctx)
                     converted-text (convert-text (url-decode text) (keyword from) (keyword to))]
                 (json/generate-string {:converted-text converted-text
                                        :original-text  text}))))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/convert" [] conversion)
  (resources "/"))

(def dev-handler (-> #'routes wrap-reload wrap-trace wrap-params))

(def handler (-> routes wrap-params))
