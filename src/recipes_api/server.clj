(ns recipes-api.server
  (:require
   [ring.adapter.jetty :as jetty]
   [integrant.core :as ig]
   [environ.core :refer [env]]
   [recipes-api.router :as router]))

(defn app [env]
  (router/routes env))

(defmethod ig/init-key :server/jetty [_ {:keys [handler port]}]
  (println (str "\nServer is running on port " port))
  (jetty/run-jetty handler {:port port
                            :join? false}))

(defmethod ig/prep-key :server/jetty [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/init-key :recipes/app [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgress [_ config]
  (println "\nConfigured db")
  (:jdbc-url config))

(defmethod ig/halt-key! :server/jetty [_ jetty]
  (println "\nServer is stopped")
  (.stop jetty))


(defn -main
  "I don't do a whole lot ... yet."
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))

(comment
  (app {:request-method :get
        :uri "/"})
  (-main))
