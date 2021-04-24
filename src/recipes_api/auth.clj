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
        :throw-exceptions false
        :body (m/encode "application/json"
                        {:client_id auth0-manage-client-id
                         :client_secret auth0-manage-client-secret
                         :audience auth0-audience
                         :grant_type "client_credentials"})}
       (http/post auth0-token-url)
       m/decode-response-body
       :access_token))

(defn get-manage-role-id [env token]
  (let [{:keys [auth0-api-base-url auth0-manage-role-name]} env]
    (->> {:headers {"Authorization" (str "Bearer " token)}
          :throw-exceptions false
          :content-type :json
          :cookie-policy :standard}
         (http/get (str auth0-api-base-url "/api/v2/roles"))
         m/decode-response-body
         (filter (fn [role]
                   (= (:name role) auth0-manage-role-name)))
         first
         :id)))

(comment
  (:auth0-manage-role-name env)
  (get-management-token env)
  (let [{:keys [auth0-api-base-url auth0-manage-role-name]} env
        token (get-management-token env)]
    (->> {:headers {"Authorization" (str "Bearer " token)}
          :throw-exceptions false
          :content-type :json
          :cookie-policy :standard}
         (http/get (str auth0-api-base-url "/api/v2/roles"))
         m/decode-response-body
         (filter (fn [role]
                   (= (:name role) auth0-manage-role-name)))
         first
         :id))
  (let [uid "auth0|607ac0f60686bf00757e502b"
        management-token (get-management-token env)
        manage-role-id (get-manage-role-id env management-token)]
    (->> (http/post (str (:auth0-api-base-url env) "/api/v2/users/" uid "/roles")
                    {:body (m/encode "application/json"
                                     {:roles [manage-role-id]})
                     :content-type :json
                     :cookie-policy :standard
                     :throw-exceptions false
                     :headers {"Authorization" (str "Bearer " management-token)}})))
  (get-test-token env))
