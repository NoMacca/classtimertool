(ns classtimertool.db
  (:require
   [re-frame.core :as re-frame]
   ))

(def default-db
  {

   :now {:hours 0 :minutes 0 :seconds 0}
   :class-timer {
                 :running-id 1
                 :running [
                           ;; {
                           ;;  :id 2
                           ;;  :name "10m Quick timer"
                           ;;  :start "00:00"
                           ;;  :end "00:10"
                           ;;  :length 10
                           ;;  }
                           ]
                 :class-id 1
                 :classes [
                           ;; {
                           ;;  :id 1
                           ;;  :name "God"
                           ;;  :start {:hours 14 :minutes 00 :seconds 0}
                           ;;  :end {:hours 15 :minutes 30 :seconds 0}
                           ;;  :length {:hours 1 :minutes 30 :seconds 0}
                           ;;  }
                           ;; {
                           ;;  :id 2
                           ;;  :name "Man"
                           ;;  :start {:hours 15 :minutes 00 :seconds 0}
                           ;;  :end {:hours 15 :minutes 30 :seconds 0}
                           ;;  :length {:hours 0 :minutes 30 :seconds 0}
                           ;;  }
                           ;;  :id 1
                           ;;  :name "Year 7 Digital Technology"
                           ;;  :start-time "12:20"
                           ;;  :end-time "15:25"
                           ;;  :length 50
                           ;;  }
                           ;; {
                           ;;  :id 2
                           ;;  :name "Year 10 Digital Technology"
                           ;;  :start-time "12:20"
                           ;;  :end-time "15:25"
                           ;;  :length 20
                           ;;  }
                           ]}
   })




;; -- Local Storage  ----------------------------------------------------------
(def ls-key "local-store-classes")                         ;; localstore key

(defn classtimertool->local-store
  "Puts todos into localStorage"
  [classes]
  ;; (js/console.log (str "Hello" classes))
  (.setItem js/localStorage ls-key (str classes)))     ;; sorted-map written as an EDN map

;; -- cofx Registrations  -----------------------------------------------------
;; (re-frame/reg-cofx
;;  :local-store-classes
;;  (fn [cofx _]
;;       ;; put the localstore todos into the coeffect under :local-store-todos
;;    (assoc cofx :local-store-classes
;;              ;; read in todos from localstore, and process into a sorted map
;;           (into (sorted-map)
;;                 (some->> (.getItem js/localStorage ls-key)


;;                          (cljs.reader/read-string)    ;; EDN map -> map

;;                          )))))

(re-frame/reg-cofx
 :local-store-classes
 (fn [cofx _]
   ;; put the localstore todos into the coeffect under :local-store-todos
   (let [data-from-storage (.getItem js/localStorage ls-key)]
     (if-let [parsed-data (when data-from-storage
                             (try
                               (cljs.reader/read-string data-from-storage)
                               (catch js/Error e
                                 (js/console.error "Error parsing data from local storage:" e)
                                 nil)))]
       (assoc cofx :local-store-classes (into (sorted-map) parsed-data))
       cofx))))
