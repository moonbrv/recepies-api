(ns recipes-api.auth
  (:require
   [environ.core :refer [env]]
   [clj-http.client :as http]
   [muuntaja.core :as m]))

(defn get-test-token [{:keys [auth0-token-url auth0-test-token-client-id
                              auth0-audience auth0-test-token-username
                              auth0-test-token-password]}]
  (-> (http/post auth0-token-url
                 {:content-type :json
                  :cookie-policy :standard
                  :body (m/encode "application/json"
                                  {:client_id auth0-test-token-client-id
                                   :audience auth0-audience
                                   :grant_type "password"
                                   :username auth0-test-token-username
                                   :password auth0-test-token-password
                                   :scope "openid profile email"})})
      m/decode-response-body
      :access_token))

(defn get-management-token [{:keys [auth0-manage-client-id
                                    auth0-manage-client-secret
                                    auth0-audience
                                    auth0-token-url]}]
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id auth0-manage-client-id
                         :client_secret auth0-manage-client-secret
                         :audience auth0-audience
                         :grant_type "client_credentials"})}
       (http/post auth0-token-url)
       m/decode-response-body
       :access_token))

(comment
  (let [{:keys [auth0-manage-client-id
                auth0-manage-client-secret
                auth0-audience
                auth0-token-url]} env]
    (->> {:content-type :json
          :cookie-policy :standard
          :body (m/encode "application/json"
                          {:client_id auth0-manage-client-id
                           :client_secret auth0-manage-client-secret
                           :audience auth0-audience
                           :grant_type "client_credentials"})}
         (http/post auth0-token-url)
         m/decode-response-body
         :access_token))
  (get-test-token env))
