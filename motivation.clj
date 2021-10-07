;; Sign up process as seen on twitter.com

(defn signup-process! []
  (let [e-mail-address (.prompt js/window "E-Mail-Address?")]
    (if (valid-email-address? e-mail-address)
      (let [birthday (.prompt js/window "Birthday?")
            pw (.prompt js/window "Password?")]
        (if (good-password? pw)
          {:what :new-user
           :e-mail e-mail-address
           :birthday birthday
           :password password}
          (signup-process!)))
      ;; else
      (signup-process!))))

;; Pros and cons of the above code:
;; + declarative style
;; + synchronous reasoning
;; - limited effects (.prompt only)
;; - limited data structure support (.prompt allows for strings only)
;; - blocks (not only synchronous in reasoning but actually synchronous)

;; Let's try wishful thinking

;; missing:
;; abort

(defn signup-process []
  (let** [personal-info
          ;; a complex gui including validation
          (get-personal-info)

          code
          (create-and-send-verification-code
           (personal-info-e-mail personal-info))

          code-entered
          (get-verification-code)]
    (if (= code code-entered)
      (make-user personal-info)
      ;; ah hell no
      (signup-process))))


;; One step back
;; Implement the above process in reacl-c
;; TODO: Marlon


;; Zwischenergebnis: we're not there yet

;; What's missing?
;; Answer (fällt from Himmel): Monads

;; Mission: Implement monadic bind in reacl-c
;; Problem 1: What does that even mean? -> Define semantics
;; Problem 2: Implement the semantics

;; Example:
(bind (get-personal-info)
      (fn [pi]
        ... continue
        ))

(defn bind [m cont]
  ;; here be dragons
  )


;; Implementation technicalities
;; Tail recursion
