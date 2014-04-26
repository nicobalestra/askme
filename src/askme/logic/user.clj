(ns askme.logic.user
  (:require [noir.session :as session]
            [noir.util.crypt :as sec]
            [taoensso.timbre :as timbre])
  )

(defn is-anonymous
  "Determines whether currently we have a loggedin user"
  []
  (= :not-found (session/get :user :not-found)))

(defn get-user []
  (session/get :user))

(defn check-login [db-user username password]
  (timbre/info "Check-login " db-user " " username " " password)
  (and (= (:username db-user) username)
       (sec/compare password (:password db-user))))
