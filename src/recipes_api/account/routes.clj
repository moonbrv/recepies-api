(ns recipes-api.account.routes
  (:require
   [recipes-api.account.handlers :as account]
   [recipes-api.responses :as responses]
   [recipes-api.middleware :as mw]))

(defn routes [env]
  (let [{:keys [jwk-endpoint]
         db :jdbc-url
         auth-base-url :auth0-api-base-url} env]
    ["/account" {:swagger {:tags ["account"]}
                 :middleware [[(mw/wrap-auth jwk-endpoint)]]}
     ["" {:post {:handler (account/create-account! db)
                 :responses {204 {:body nil?}}
                 :summary "Create account"}
          :delete {:handler (account/delete-account! db auth-base-url)
                   :responses {204 {:body nil?}}
                   :summary "Delete account"}}]]))
