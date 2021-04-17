(ns recipes-api.server
  (:require
   [next.jdbc :as jdbc]
   [ring.adapter.jetty :as jetty]
   [integrant.core :as ig]
   [environ.core :refer [env]]
   [recipes-api.router :as router]))

(defn app [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :recipes/app [_ config]
  (merge config {:jwk-endpoint (env :jwk-endpoint)}))

(defmethod ig/prep-key :db/postgress [_ config]
  (merge config {:jdbc-url (env :jdbc-url)}))

(defmethod ig/init-key :server/jetty [_ {:keys [handler port]}]
  (println (str "\nServer is running on port " port))
  (jetty/run-jetty handler {:port port
                            :join? false}))

(defmethod ig/init-key :recipes/app [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgress [_ {:keys [jdbc-url]}]
  (println "\nConfigured db")
  jdbc-url
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(defmethod ig/halt-key! :server/jetty [_ jetty]
  (println "\nServer is stopped")
  (.stop jetty))


(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))

(comment
  (app {:request-method :get
        :uri "/"})
  (-main "resources/config.edn"))
