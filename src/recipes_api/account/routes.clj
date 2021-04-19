(ns recipes-api.account.routes
  (:require
   [recipes-api.account.handlers :as account]
   [recipes-api.responses :as responses]
   [recipes-api.middleware :as mw]))

(defn routes [env]
  (let [{:keys [jwk-endpoint]
         db :jdbc-url} env]
    ["/account" {:swagger {:tags ["account"]}
                 :middleware [[(mw/wrap-auth jwk-endpoint)]]}
     ["" {:post {:handler (account/create-account! db)
                 :responses {204 {:body nil?}}
                 :summary "Create account"}}]]))
