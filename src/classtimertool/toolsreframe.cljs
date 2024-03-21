(ns tools.reframetools
  (:require
   [clojure.string :as str]
   ))

(defn sdb [path]
  (fn [db [_ v]]
    (assoc-in db path v)))

(defn gdb
  [path]
  (fn [db _] (get-in db path)))

;;===============================================
;;===============================================

(defn now
  "Used to get the current time"
  []
  (let [now (js/Date.)
        hours (.getHours now)
        minutes (.getMinutes now)
        seconds (.getSeconds now)]
    {:hours hours :minutes minutes :seconds seconds}
    ))

(defn string->time
  "Used to convert input of type time into map for app-db {:minutes 120 :seconds 30}"
  [time-str]
  (let [[hours minutes seconds] (mapv #(js/parseInt %) (str/split time-str #":"))]
    {:hours hours :minutes minutes :seconds (if seconds seconds 0)}))

(defn time-left
  "Used to calculate the time left given the current time and end time"
  [now end]
  (let [now-seconds (+ (* (:hours now) 3600) (* (:minutes now) 60) (:seconds now))
        end-seconds (+ (* (:hours end) 3600) (* (:minutes end) 60) (:seconds end))
        total-seconds-left (- end-seconds now-seconds)
        hours-left (quot total-seconds-left 3600)
        remaining-seconds (mod total-seconds-left 3600)
        minutes-left (quot remaining-seconds 60)
        seconds-left (mod remaining-seconds 60)]
    {:hours hours-left :minutes minutes-left :seconds seconds-left}))

(defn timer-end
  "Used to determine that a timer has finished"
  [time]
  (let [hours (:hours time)
        minutes (:minutes time)
        seconds (:seconds time)]
  (and (= hours 0) (= minutes 0) (= seconds 0)
      )))

(defn time-used
  "Used to calculate the time used given the current time and start time"
  [now start]
  (let [now-seconds (+ (* (:hours now) 3600) (* (:minutes now) 60) (:seconds now))
        start-seconds (+ (* (:hours start) 3600) (* (:minutes start) 60) (:seconds start))
        total-seconds-used (- now-seconds start-seconds)
        hours-used (quot total-seconds-used 3600)
        remaining-seconds (mod total-seconds-used 3600)
        minutes-used (quot remaining-seconds 60)
        seconds-used (mod remaining-seconds 60)]
    {:hours hours-used :minutes minutes-used :seconds seconds-used}))

(defn end-time
 "Used to calculate the end time given the start time and length"
  [start length]
  (let [total-seconds-start (+ (* (:hours start) 3600) (* (:minutes start) 60) (:seconds start))
        total-seconds-length (+ (* (:hours length) 3600) (* (:minutes length) 60) (:seconds length))
        total-seconds-end (+ total-seconds-start total-seconds-length)
        hours-end (quot total-seconds-end 3600)
        minutes-end (quot (rem total-seconds-end 3600) 60)
        seconds-end (rem (rem total-seconds-end 3600) 60)]
    {:hours hours-end :minutes minutes-end :seconds seconds-end}))

(defmulti time-string
  "Used to convert time to a string for display"
  (fn [t time] t))

(defmethod time-string :minutes [t time]
  (let [{:keys [hours minutes seconds]} time]
    (str (+ (* 60 hours) minutes (/ seconds 60)))
    ))

(defmethod time-string :minutes-seconds [t time]
  (let [{:keys [hours minutes seconds]} time
        padded-minutes (if (< minutes 10) (str "0" minutes) (str minutes))
        padded-seconds (if (< seconds 10) (str "0" seconds) (str seconds))]
    (str padded-minutes ":" padded-seconds)
    ))

(defmethod time-string :hours-minutes [t time]
  (let [{:keys [hours minutes seconds]} time
        padded-hours (if (< hours 10) (str "0" hours) (str hours))
        padded-minutes (if (< minutes 10) (str "0" minutes) (str minutes))]
    (str padded-hours ":" padded-minutes)))

(defmethod time-string :hours-minutes-seconds [t time]
  (let [{:keys [hours minutes seconds]} time
        padded-hours (if (< hours 10) (str "0" hours) (str hours))
        padded-minutes (if (< minutes 10) (str "0" minutes) (str minutes))
        padded-seconds (if (< seconds 10) (str "0" seconds) (str seconds))
        ]
    (str padded-hours ":" padded-minutes ":" padded-seconds)
    ))



(defn running? [now start end]
  (let [now-seconds (+ (* (:hours now) 3600)
                       (* (:minutes now) 60)
                       (:seconds now))
        start-seconds (+ (* (:hours start) 3600)
                         (* (:minutes start) 60)
                         (:seconds start))
        end-seconds (+ (* (:hours end) 3600)
                       (* (:minutes end) 60)
                       (:seconds end))]
    (and (<= start-seconds now-seconds)
         (<= now-seconds end-seconds))))


(defn ended? [now end]
  (let [now-seconds (+ (* (:hours now) 3600)
                       (* (:minutes now) 60)
                       (:seconds now))
        end-seconds (+ (* (:hours end) 3600)
                       (* (:minutes end) 60)
                       (:seconds end))]
    (< end-seconds now-seconds)
    ))


;; Test the function
;; (def end-var {:hours 13 :minutes 40 :seconds 40})
;; (def now-var {:hours 11 :minutes 30 :seconds 30})
;; (def start-var {:hours 10 :minutes 30 :seconds 30})
;; (running? now-var start-var end-var)

;; ;;Testing functions
;; (def now-var {:hours 11 :minutes 30 :seconds 29})
;; (def start-var {:hours 10 :minutes 30 :seconds 30})
;; (def end-var {:hours 13 :minutes 40 :seconds 40})
;; (time-left now-var end-var)
;; (time-used now-var start-var)

;; (time-string :hours-minutes {:hours 4 :minutes 10 :seconds 30})
;; (time-string :hours-minutes-seconds {:hours 4 :minutes 10 :seconds 30})
;; (time-string :minutes {:hours 2 :minutes 10 :seconds 30})

;; (now)
;; (def time-foo "02:01")
;; (string->time time-foo)

;; (def start {:hours 16, :minutes 50, :seconds 29})
;; (def length {:hours 0, :minutes 0, :seconds 33})

;; (def end {:hours 16, :minutes 50, :seconds 59})

;; (end-time start length)

;; :end {:hours 16, :minutes 50, :seconds 29}
