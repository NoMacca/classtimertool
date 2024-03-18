(ns classtimertool.events
  (:require
   [re-frame.core :as re-frame]
   [tools.reframetools :refer [sdb gdb]]
   [tools.reframetools :as h]
   ;; [day8.re-frame.tracing :refer-macros [fn-traced]]
   [classtimertool.db :as db]
   ;; [classtimertool.helpers :as h]
   ;; [re-frame.alpha :as reframe]
   ;; [re-frame.alpha :refer [reg-sub reg-event-db reg-event-fx reg-flow inject-cofx path after sub]]
   ;; [classtimertool.db :as db]
   ))

;; -- Interceptors --------------------------------------------------------------
;;Puts classes into local store
(def ->local-store (re-frame/after db/classtimertool->local-store))

(def interceptors [
                   (re-frame/path :class-timer)
                   ->local-store])
;; LOCAL STORE
(re-frame/reg-event-fx
 :initialize-db
 [(re-frame/inject-cofx :local-store-classes)
  ]

 (fn [{:keys [db local-store-classes]} _]
   {:db
    (assoc db/default-db :class-timer local-store-classes)}))

;;SUBSCRIPTIONS
(re-frame/reg-sub :now (gdb [:now]))

(defn class-timer
  [db _]
  (:class-timer db))
(re-frame/reg-sub :class-timer class-timer)

(re-frame/reg-sub
 :running
 :<-[:class-timer]
 (fn [db _ ]
   (:running db)
   ))

(re-frame/reg-sub
 :classes
 :<-[:class-timer]
 (fn [db _ ]
   (:classes db)
   ))

;;EVENTS
(re-frame/reg-event-db :now (sdb [:now]))

(re-frame/reg-event-db
 :running-quick-timer
 interceptors
 (fn [db [_ length]]
   (let [
         id (:running-id db)
         name "Quick Timer"
         start (h/now)
         end (h/end-time start length)]
     (js/console.log (str "ID: "id))

     (-> db
         (update :running-id inc) ;;(inc (:running-id db))
         (assoc  :running (conj (:running db)
                                {:id id :name name :start start :end end :length length}
                                ))))))

(defn remove-map-by-id [id data]
  (into [] (filter #(not= (:id %) id) data)))

(re-frame/reg-event-db
 :kill-timer
 interceptors
 (fn [db [_ id]]
   (assoc db :running (remove-map-by-id id (:running db)))))

;;CLASSES
(defn find-map-by-id [id data]
  (into {} (filter #(= (:id %) id) data)))

;;Add class as a timer
(re-frame/reg-event-db
 :run-class
 interceptors
 (fn [db [_ class-id]]
   (let [
         class (find-map-by-id class-id (:classes db))
         id (:running-id db)
         name (:name class)
         start (:start class)
         end (:end class)
         length (:length class)]

     (-> db
         (update :running-id inc)
         (assoc  :running (conj (:running db)
                                {:id id :name name :start start :end end :length length}
                                ))))))
(re-frame/reg-event-db
 :add-class
 interceptors
 (fn [db [_ class]]
   (let [
         id (:class-id db)
         name (first class)
         start (h/string->time (second class))
         end (h/string->time (last class))
         length (h/time-left start end)
         ]
     (-> db
         (update :class-id inc)
         (assoc  :classes (conj (:classes db)
                                {:id id :name name :start start :end end :length length}
                                ))))))

(re-frame/reg-event-db
 :delete-class
 interceptors
 (fn [db [_ id]]
   (assoc db :classes (remove-map-by-id id (:classes db)))))
