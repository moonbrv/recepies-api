(ns recipes-api.account.handlers
  (:require
   [muuntaja.core :as m]
   [ring.util.response :as rr]
   [recipes-api.auth :as auth]
   [recipes-api.account.db :as account-db]
   [clj-http.client :as http]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]]))

(defn create-account! [db]
  (fn [request]
    (let [{:keys [sub picture]
           uname :name} (:claims request)]
      (account-db/insert-account! db {:uid sub
                                      :name uname
                                      :picture picture})
      (rr/status 204))))

(defn delete-account! [db {:keys [auth0-api-base-url] :as env}]
  (fn [request]
    (let [uid (get-uid request)
          oauth-result (http/delete (str auth0-api-base-url "/api/v2/users/" uid)
                                    {:headers {"Authorization" (str "Bearer " (auth/get-management-token env))}})]
      (when (= (:status oauth-result) 204)
        (account-db/delete-account! db uid)
        (rr/status 204)))))

(defn update-role-to-cook! [{:keys [auth0-api-base-url] :as env}]
  (fn [request]
    (let [uid (get-uid request)
          management-token (auth/get-management-token env)
          manage-role-id (auth/get-manage-role-id env management-token)]
      (http/post (str auth0-api-base-url "/api/v2/users/" uid "/roles")
                 {:body (m/encode "application/json"
                                  {:roles [manage-role-id]})
                  :content-type :json
                  :cookie-policy :standard
                  :throw-exceptions false
                  :headers {"Authorization" (str "Bearer " management-token)}}))))
