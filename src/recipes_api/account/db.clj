(ns recipes-api.account.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [recipes-api.utils :as u]))

(defn insert-account! [db account]
  (sql/insert! db :account account))

(defn delete-account! [db id]
  (sql/delete! db :account {:uid id}))
