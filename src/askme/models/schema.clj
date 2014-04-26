(ns askme.models.schema
  (:refer-clojure :exclude [clojure.java.jdbc])
  (:require [clojure.java.jdbc :as sql]
            [taoensso.timbre :as timbre]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/askme"
   :user "nicoletto"
   :password ""})

(defn initialized? []
  (sql/with-connection db-spec
      (sql/with-query-results rows
         ["select count(*) from information_schema.tables where table_catalog='askme' and table_schema='public'"]
             (pos? (:count (first rows)))
                        )))
