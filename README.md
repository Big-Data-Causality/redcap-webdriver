# redcap-webdriver

[![Build Status](https://github.com/Big-Data-Causality/redcap-webdriver/actions/workflows/clojure.yml/badge.svg)](https://github.com/Big-Data-Causality/redcap-webdriver/actions)
[![Clojars Project](https://img.shields.io/clojars/v/com.bigdatacausality/redcap-webdriver.svg)](https://clojars.org/com.bigdatacausality/redcap-webdriver)

REDCap api polyfill library for Clojure using the `etaoin` webdriver library.

## Usage

#### Webdriver Controls

```clojure
(require '[redcap.webdriver :as rw])

(rw/get-driver)   ;; gets the current webdriver or creates a new one if it doesn't exist

(rw/end-driver)   ;; terminates the current webdriver

(rw/reset-driver) ;; initialises a new webdriver
```

#### Environment Controls

The default env would always be from the `.env-site` file in a project directory

```clojure
(require '[redcap.webdriver :as rw])

(rw/get-env-site) ;; reads env from `.env-site`
;; => {:site "https://redcapdemo.vanderbilt.edu/"
;;     :username "test0003@zcaudate.xyz"
;;     :password "Acb8642793"}

(rw/get-env-site ".env-custom") ;; reads env from `.env-custom`
```

#### Signup for a New Email Account

```clojure
(require '[redcap.webdriver-signup :as signup])

(signup/signup-trial
 {:email "john.smith@esting.com"
  :first-name "John"
  :last-name   "Smith"
  :organisation-name "-"
  :organisation-type :for_profit
  :country "AU"
  :reason  :check_out_latest_features})
;; => <EMAIL FROM REDCAP PROVIDER WITH NEW ACCOUNT DETAILS>
```

#### Login

```clojure
(require '[redcap.webdriver :as rw])

(rw/login)
;; => <LOGIN WITH WEBDRIVER>

(rw/login {:site "https://redcapdemo.vanderbilt.edu/"
           :username "test0003@zcaudate.xyz"
           :password "Acb8642793"})
;; => <LOGIN WITH WEBDRIVER USING CUSTOM OPTIONS>

```

#### List Projects

```clojure
(require '[redcap.webdriver :as rw])

(rw/project-list)
;; lists currently active projects
=> [{:name "Project 001", :pid "56153"}
    {:name "Project 002", :pid "56154"}]
```

#### New Project

```clojure
(require '[redcap.webdriver :as rw])

(rw/project-new {:title (str "Project " (h/uuid))
                   :notes "Sample"})
;; => <CREATES NEW PROJECT WITH LOGIN>
```

#### Project API Token

The token is needed for API access

```clojure
(require '[redcap.webdriver :as rw])

(rw/project-api-token "56154")
;; => <API TOKEN>
```

#### Delete Project

```clojure
(require '[redcap.webdriver :as rw])

(rw/project-delete "56154")
;; => <DELETES SPECIFIC PROJECT>

(rw/project-delete-all)
;; => <DELETES ALL PROJECTS>
```

## License

Copyright © 2024 Chris Zheng

Distributed under the MIT License.
