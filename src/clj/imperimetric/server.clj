(ns imperimetric.server
  (:require [imperimetric.handler :refer [handler dev-handler]]
            [config.core :refer [env]]
            [imperimetric.frinj-setup :refer [frinj-setup!]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (frinj-setup!)
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty handler {:port port :join? false})))

(defn dev-main []
  (frinj-setup!)
  (run-jetty dev-handler {:port 4000 :join? false}))
