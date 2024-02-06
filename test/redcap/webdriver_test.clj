(ns redcap.webdriver-test
  (:use code.test)
  (:require [redcap.webdriver :as main]
            [redcap.webdriver-base :as base]
            [std.lib :as h]))

^{:refer redcap.webdriver/login :added "0.1"}
(fact "logs in to the site"
  ^:hidden

  (main/login)
  => map?

  (base/is-logged-out? (base/get-driver))
  => false)

^{:refer redcap.webdriver/logout :added "0.1"}
(fact "logs out of the site"
  ^:hidden
  
  (main/logout)
  => map?

  (base/is-logged-out? (base/get-driver))
  => true)

^{:refer redcap.webdriver/project-create :added "0.1"}
(fact "creates a project given environment"
  ^:hidden
  
  (do (main/login)
      (main/project-create {:title (str "Project " (h/uuid))
                            :notes "Test Project"}))
  => (contains
      {:url #"https://redcapdemo.vanderbilt.edu/redcap_v",
       :pid string?}))

^{:refer redcap.webdriver/project-api-enabled? :added "0.1"}
(fact "checks that the project api is enabled")

^{:refer redcap.webdriver/project-api-toggle :added "0.1"}
(fact "toggles the project api")

^{:refer redcap.webdriver/project-api-token :added "0.1"}
(fact "gets the project token")

^{:refer redcap.webdriver/project-list :added "0.1"}
(fact "lists all projects in the given environment"
  ^:hidden

  (main/project-list)
  => vector?)

^{:refer redcap.webdriver/project-delete :added "0.1"}
(fact "deletes a newly created project"
  ^:hidden

  (do (main/login)
      (def +out+
        (main/project-create {:title (str "Project " (h/uuid))
                              :notes "Test Project"}))
      (main/project-delete (:pid +out+)))
  => vector?)

^{:refer redcap.webdriver/project-delete-all :added "0.1"}
(fact "deletes all projects in the environment"
  ^:hidden

  (do (main/login)
      (def +out+
        (main/project-create {:title (str "Project " (h/uuid))
                              :notes "Test Project"}))
      (main/project-delete-all))
  => vector?)

^{:refer redcap.webdriver/project-new :added "0.1"}
(fact "creates a brand new project"
  ^:hidden

  (do (main/login)
      (def +out+
        (main/project-new {:title (str "Project " (h/uuid))
                           :notes "Sample"})))
  +out+
  => (contains-in
      {:token string?
       :url string?,
       :pid string?}))


(comment
  (e/exists? (base/get-driver) {:xpath "//form[@name='createdb']//button"})
  (e/click (base/get-driver) {:xpath "//form[@name='createdb']//button"})
  (e/get-url (base/get-driver)))

(comment
  (logout)
  (login)
  (project-list)
  [{:name "Test Project", :pid "55912"}]
  (bas)
  (project-delete-all)
  (project-create {:title "Test Project"
                   :notes "hello"})

  (base/get-env-site)
  
  
  (project-api-enabled? (:pid base/*current-project*))
  (project-api-toggle (:pid base/*current-project*))
  (project-api-token (:pid base/*current-project*))
  
  (e/get-element-attrs (base/get-driver)
                       {:tag :input :id "apiTokenId"}
                       )
  
  (e/click driver
           {:xpath "//div[@id='apiReqBoxId']//div[@class='chklistbtn']"})
  (e/click (base/get-driver)
           {:xpath "//div[@id='apiReqBoxId']//div[@class='chklistbtn']/button"})
  (e/exists? (base/get-driver)
             {:xpath "//div[@id='apiReqBoxId']//div[@class='chklistbtn']"})
  (e/exists? (base/get-driver)
             {:xpath "//div[@id='apiReqBoxId']//div[@class='chklistbtn']/button"})
  (e/get-element-attr (base/get-driver)
                      {:tag :input :id "apiTokenId"}
                      "value")
  
  [{:name "Project 67b79ea9-674b-401c-b69e-d1939c5c7220", :code "55828"}]
  
  (project-delete "55820")
  (project-delete "55822")
  (project-delete "55821")
  (project-delete "55819")
  
  (base/end-driver)
  (base/get-driver {:size [1200 1200]})
  (base/get-driver {:headless true
                    :size [1200 1200]})
  (e/get-element-tex (base/get-driver)
                     )
  (e/get-window-size (base/get-driver))
  
  (e/set-window-size (base/get-driver)
                     {:width 1200 :height 1200})
  
  (e/click (base/get-driver)
           {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=2]"})
  (e/get-element-text (base/get-driver)
                      {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=2]"})
  
  (e/click (base/get-driver)
           {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=2]"})
  (e/query-all (base/get-driver)
               {:xpath "//div[@class='ui-dialog-buttonset']//button"}))



(comment

  (e/exists? (base/get-driver) "//a[text()='API Playground']")
  
  (e/get-element-attr (base/get-driver)
                      {:tag :input :id "apiTokenId"}
                      "value")

  (e/exists? (base/get-driver)
             {:xpath "//div[@id='apiReqBoxId']"})
  (e/click (base/get-driver)
           {:xpath "//div[@id='apiReqBoxId']/div[@class='chklistbtn']/button"})
  
  (e/exists? (base/get-driver)
             {:xpath "//div[@id='apiReqBoxId']"})
  
  
  (e/exists? (base/get-driver)
             {:xpath "//div[@id='apiReqBoxId']"})
  
  (e/click (base/get-driver)
           {:xpath "//div[@id='tooltipBtnSetCustom']/button"})

  (e/click (base/get-driver)
           {:tag :input
            :name "api_import"})
  (e/click (base/get-driver)
           {:tag :input
            :name "api_export"})
  
  (project-api-token "55828")
  
  (project-enable-api )
  (login)
  (project-api-enabled? "55828")
  (project-api-toggle "55828"))

