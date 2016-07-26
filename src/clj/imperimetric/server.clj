(ns imperimetric.server
  (:require [imperimetric.handler :refer [handler dev-handler]]
            [config.core :refer [env]]
            [frinj.jvm :refer [frinj-init!]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (frinj-init!)
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty handler {:port port :join? false})))

(defn dev-main []
  (frinj-init!)
  (run-jetty dev-handler {:port 4000 :join? false}))
