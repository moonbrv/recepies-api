(ns recipes-api.auth
  (:require
   [environ.core :refer [env]]
   [clj-http.client :as http]
   [muuntaja.core :as m]))

(defn get-test-token [{:keys [auth0-token-url auth0-client-id auth0-audience
                              auth0-grant-type auth0-username auth0-password
                              auth0-scope]}]
  (-> (http/post auth0-token-url
                 {:content-type :json
                  :cookie-policy :standard
                  :body (m/encode "application/json"
                                  {:client_id auth0-client-id
                                   :audience auth0-audience
                                   :grant_type auth0-grant-type
                                   :username auth0-username
                                   :password auth0-password
                                   :scope auth0-scope})})
      m/decode-response-body
      :access_token))

(comment
  (get-test-token env))
