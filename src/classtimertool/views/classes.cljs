(ns classtimertool.views.classes
  (:require
   [reagent.core  :as reagent]
   [re-frame.core :as re-frame]
   [tools.reframetools :as h]
   [tools.viewtools :as vt]
   [cljs.pprint :as pp]
   [classtimertool.stylesgarden :as gstyle]
   [classtimertool.db :as db]
   ;; [tools.reframetools :as h]
   ;; [re-frame.alpha :refer [subscribe dispatch sub]]
   [reitit.frontend.easy :as rtfe]
   ))


(defn class-layout [{:keys [id name start end length]}]
  [:div.grid.grid-cols-5.gap-1
   [:div.col-start-1.col-span-2
    [:a.col-span-full {:href (rtfe/href :routes/#frontpage)}
     [:button.text-blue-500.underline.hover:text-blue-700
      {
       :id id
       :href (rtfe/href :routes/#frontpage)
       :on-click #(re-frame/dispatch [:run-class id])
       }
      name]]]
   [:div.col-start-3 (str (h/time-string :hours-minutes start) "-" (h/time-string :hours-minutes end))]
   [:div.col-start-4 (str (h/time-string :minutes length) " mins")]
   [:div.col-start-5 [:button {:on-click #(re-frame/dispatch [:delete-class id])} "x"]]
   ]
  )

(defn class-list []
(let [classes @(re-frame/subscribe [:classes])]
 [:div.grid.grid-cols-5.p-6.m-4.rounded-xl.shadow-lg.items-center
  [:div.col-start-1.col-span-4 [:h2.font-bold "Classes"]]
  [:div.col-start-1.col-span-2 "Name"]
  [:div.col-start-3 "Time"]
  [:div.col-start-4 "Length"]
  [:div.col-start-5 ""]
  [:div.col-span-full
   (for [class  classes]
     ^{:key (:id class)} [class-layout class])]]))

(defn add-class []
  (let [open-dialog? (reagent/atom false)
        title  (reagent/atom nil)
        start  (reagent/atom nil)
        end  (reagent/atom nil)
        ]
  (fn []
    (if @open-dialog?
      [:div.fixed.top-0.left-0.w-full.h-full.bg-black.bg-opacity-50.flex.items-center.justify-center
       [:dialog.grid.grid-cols-2.gap-1.bg-white.p-6.rounded.shadow-lg {:id "my-dialog"
                                                                       :open @open-dialog?
                                                                       }

        [:div.col-start-1 [:h2.font-bold "Create a class"]]
        [:div.col-start-2.text-right [:button.w-6.h-6.rounded.bg-cyan-200
                                      {:on-click #(reset! open-dialog? false)
                                       ;; #(re-frame/dispatch [:close-dialog])
                                       }
                                      "X"]]
        [:div.col-span-full "Name:"]
        [:div.col-span-full
         [:input.mt-1.block.w-full.px-3.py-2.bg-white.border.border-slate-300.rounded-md.text-sm.shadow-sm.placeholder-slate-400.focus:outline-none.focus:border-sky-500.focus:ring-1.focus:ring-sky-500.disabled:bg-slate-50.disabled:text-slate-500.disabled:border-slate-200.disabled:shadow-none.invalid:border-pink-500.invalid:text-pink-600.focus:invalid:border-pink-500.focus:invalid:ring-pink-500
          {:type "text"
           ;; :value @title
           :on-change #(reset! title (-> % .-target .-value))
           }]]

        [:div.col-start-1 "Start"]
        [:div.col-start-2 "End"]

        [:div.col-start-1       [:input.mt-1.block.w-full.px-3.py-2.bg-white.border.border-slate-300.rounded-md.text-sm.shadow-sm.placeholder-slate-400.focus:outline-none.focus:border-sky-500.focus:ring-1.focus:ring-sky-500.disabled:bg-slate-50.disabled:text-slate-500.disabled:border-slate-200.disabled:shadow-none.invalid:border-pink-500.invalid:text-pink-600.focus:invalid:border-pink-500.focus:invalid:ring-pink-500
                                 {
                                  :type "time"
                                  :value @start
                                  :on-change #(reset! start (-> % .-target .-value))
                                  :autocomplete "off"
                                  ;; :value "tbone"
                                  ;; :disabled true
                                  }]]

        [:div.col-start-2       [:input.mt-1.block.w-full.px-3.py-2.bg-white.border.border-slate-300.rounded-md.text-sm.shadow-sm.placeholder-slate-400.focus:outline-none.focus:border-sky-500.focus:ring-1.focus:ring-sky-500.disabled:bg-slate-50.disabled:text-slate-500.disabled:border-slate-200.disabled:shadow-none.invalid:border-pink-500.invalid:text-pink-600.focus:invalid:border-pink-500.focus:invalid:ring-pink-500
                                 {
                                  :type "time"
                                  ;; :value "tbone"
                                  :value @end
                                  :on-change #(reset! end (-> % .-target .-value))
                                  ;; :disabled true
                                  }]]

        [:div.col-start-2.text-right [:button.rounded.bg-blue-600.py-1.w-full.hover:bg-blue-700
                                      {:on-click #(do
                                                    (reset! open-dialog? false)
                                                    (re-frame/dispatch [:add-class [@title @start @end]])
                                                      )
                                       }
                                      "Add"]]]]
      [:div
       [:button.btn.btn-primary.btn-lg.bg-red-500.rounded.rounded.p-6.hover:bg-blue-700
         {:on-click #(reset! open-dialog? true)
         :style {:position "fixed" :bottom "10%" :right "10%"}}
        "Create Class"]]
      ))))

(defn main []
  [:div
   ;; [:h2.text-4xl "Classes"]
   [class-list]
   [add-class]
   ;; [button]
   ])
