; hmmmmm. A good next step might be writing a bunch of code that you want to make work.
; so like, decimal numbers like 10.3 can be handled as a HINT-breakable macro for . that takes symbols 10 and 3 and returns 10.3
; solve the problem of it's-icky-to-force-macros-to-return-lists by making them be generators?
  ; interacts weirdly with last-value-stated-is-returned but shouldn't be a big issue
; -
; okay so the problem is: both = and qprint are taking arity tokens, but that's bad: = should take 1 token and 1 value and qprint should take 1 value
; that's... weird
; hmm. what else is weird?
;   clojure::case might be
; -
; phase "rudimentary read" or maybe "reader macros" or maybe "parse"
; phase expand foundation macros
; phase discard whitespace
; phase expand normal macros
; phase evaluation (sans macros)
; -
; how do we want to do metadata? do we want to do metadata? should like in kdef, things like :arity be metadata on the :val ?
; foundation macros currently have to reach way into the impl guts; this is probably not good
; consider: implement [normal macros, :back_grab, :autocall] with foundation macros?
; DURR maybe comma shouldn't be a barrier?
  ; and then maybe rename soft and hint to hard and soft? and rename "barrier" to "whitespace"? because duh? or: duh *not*????????
; may be failing to look inside vecs, Quotes, NonMacros?
; next: think about syntax for def
  ; maybe look in lang_notes
  ; python-style would be:
    ; def name(a,b,c): <body>
  ; really, we have "def" vs "=": "define" vs "assign"; two very distinct concepts. This is an issue.
; next: how DO we want to handle whitespace? big questions.
; hmm. in clojure, we have no access to _anything_ pattern-matchy or parse-y _at all_, unless we hack a reader macro
; urgh. this is hurting. maybe just think about this later
; hmm. steps?:
  ; harden the current situation
    ; <done> at-most-one-consecutive-whitespace-token
    ; "some"
      ; right now, it's defined as "alter the world however you like"
        ; this is ...questionable
        ; mm, but that could work if you regularly cut off the world
          ; ehhhhh.
        ; this... could still be suitable at reader-macro-level, i suppose

  ; any order:
    ; implement vecs as vararg space-macros (because they only care about forms)
    ; figure a cleaner way to do Quote and NonMacro if possible
  ; decide where to go from here?
    ; start macroizing the parser? is that even a possible thing?
  ; ARGH i think i am SERIOUSLY messed up: what happens (in regards to the flow from characters to values) when you have function definitions?
  ; okay. you wanted to do this the route of start-simple-and-build-up-from-there.
    ; 

; imagine [out in] as ---out--->head <us> tail---in--->

(defmacro fetch [] `(dq_pop_tail ~'in))
(defmacro yield [v] `(dq_push ~'out ~v))

(deftype Quote [v] Object (toString [me] (str "Q:"v)))
(deftype NonMacro [v] Object (toString [me] (str "NM:"v)))

(def defs (atom {}))
(defn kdef [name v] (dl
  if<- v (= (v :arity) :until_soft)
    (dl
      (assert (v :autocall))
      (assert (not (v :fnd_macro)))
      (assoc v :fnd_macro
        {:arity :full_guts
         :val (fn [^Deque out ^Deque in] (dl
                it <- (fetch)
                (yield (ksymbol "call"))
                (#>push_vec_of_l_getuntil out in #{NEWLINE} [(Quote. it)] false)
                ))}))
  (swap! defs assoc (ksymbol name) v)))
(kdef "("
  {:fnd_macro
     {:arity :full_guts
      :val (fn [^Deque out ^Deque in] (dl
             t <- (fetch)
             (if-not (isa (dq_peek out) KSymbol)
               (yield t)
               (dl
                 t <- (Quote. (dq_pop out))
                 (yield (ksymbol "call"))
                 (#>push_vec_of_l_getuntil out in #{(ksymbol ")")} [t] true)
                 ))
             ))
      }
   })
(kdef "["
  {:fnd_macro
     {:arity :full_guts
      :val (fn [^Deque out ^Deque in] (dl
             t <- (fetch)
             (if (or (dq_empty out) (#{NEWLINE SPACE} (dq_peek out)))
               (#>push_vec_of_l_getuntil out in #{(ksymbol "]")} [] true)
               (dl
                 (yield (ksymbol "subscript"))
                 (#>push_vec_of_l_getuntil out in #{(ksymbol "]")} [] true)
                 )
               )))
      }
   })
(kdef "."
  {:fnd_macro
     {:arity :full_guts
      :val (fn [^Deque out ^Deque in] (dl
             t <- (fetch)
             ; i don't want to bother trying to get this done before efm_sym_resolution atm, so right now i'll just take an int and KSymbol
             [a b] <- [(dq_peek_ out) (dq_peek_tail_ in)]
             (if (and (isa a Integer) (isa b KSymbol) (<= (i\0) (int (. (. b name) charAt 0)) (i\9)))
               (dl
                 (dq_pop out) (fetch)
                 (yield (Double/parseDouble (str a"."(. b name))))
                 )
               (yield t)
               )
             ))
      }
   })
(kdef "->"
  {:macro
     {:arity 1
      :val #(dl [(NonMacro. (ksymbol "->")) (Quote. %)])
      }
   :autocall true
   :arity :infix1
   :val (fn [v sym] (dl
          (cast KSymbol sym)
          (swap! defs assoc sym {:val v})
          v
          ))
   })
(kdef "="
  {:macro
     {:arity :back_grab
      :val #(dl [(NonMacro. (ksymbol "=")) (Quote. %)])
      }
   :autocall true
   :arity 2
   :val (fn [sym v] (dl
          (cast KSymbol sym)
          (swap! defs assoc sym {:val v})
          v
          ))
   })
(kdef "inc"
  {:autocall true
   :arity 1
   :val #(+ % 1)
   })
(kdef "add_val" {:val +})
(kdef "rprint"
  {:autocall true
   :arity :until_soft
   :val #(dl (apply prn %&) (last %&))
   })
(kdef "call"
  {:autocall true
   :arity 1
   :val (fn [[name & args]] (apply ((@defs name) :val) args))
   })
(kdef "subscript"
  {:autocall true
   :arity :infix1
   :val (fn [v [sub]] (nth v sub))})

(defn push_vec_of_l_getuntil [^Deque out ^Deque in test l kill_sentinel]
  (#>get_until_and_run out in #>efm_mod test
    (fn [out r t _] (dl
      (yield (vec (concat l r)))
      (if kill_sentinel (dq_pop_tail t))
      ))))
(defn get_n_and_run [^Deque out ^Deque in with n body]
  (#>get_some_and_run out in with #(if (< (dq_len %) n) nil n) body))
(defn get_until_and_run [^Deque out ^Deque in with test body]
  (#>get_some_and_run out in with
    ;! kinda O(n^2); could be avoided if r was a custom deque
    #(dl ai <- (atom nil) (doseq-indexed [i v %] (if (and (nil? @ai) (test v)) (reset! ai i))) @ai)
    body))
(defn get_some_and_run [^Deque out ^Deque in with index_calc body] (dl ; decls for `body` may wish to use parameter names [out r t in]
  r <- (dq)
  (loop [] (dl
    (with r in)
    i <- (index_calc r)
    (if (nil? i) (recur)
      (dl
        buf <- (dq) (dotimes [i (- (dq_len r) i)] (dq_push_tail buf (dq_pop r)))
        (body out r buf in)
        (dmap #(yield %) buf)
        ))))
  nil))

(defn efm_sym_resolution [sym] (dl
  s <- (. sym name)
  (if (<= (i\0) (int (. s charAt 0)) (i\9))
    (Integer/parseInt s)
    sym
    )))

(defn efm_mod [^Deque out ^Deque in] (dl
  it <- (fetch)
  (if (isa it KSymbol)
    (dl d <- (@defs it)
      (if d
        (if (d :fnd_macro)
          (dl d <- (d :fnd_macro)
            (assert (= (d :arity) :full_guts))
            (dq_push_tail in it)
            ((d :val) out in)
            )
          (yield it))
        (yield (efm_sym_resolution it))
        ))
    (yield it)
    )))
(defn dw_some  [^Deque out ^Deque in] (dl
  it <- (fetch)
  (cond
    (vector? it) (yield (vec (discard_whitespace (dq it))))
    (#{NEWLINE SPACE} it) nil
    :else (yield it)
    )))
(defn enm_mod [^Deque out ^Deque in] (dl
  it <- (fetch)
  (cond
    (vector? it) (yield (vec (expand_normal_macros (dq it))))
    (and (isa it KSymbol) (@defs it) ((@defs it) :macro))
      (dl d <- ((@defs it) :macro)
        (dmap #(dq_push_tail in %) (reverse (apply (d :val)
          (if (= (d :arity) :back_grab)
            [(dq_pop out)]
            (doall (repeatedly (d :arity) #(fetch)))
            )))))
    :else (yield it)
    )))
(defn esm_mod [^Deque out ^Deque in] (dl
  it <- (fetch)
  (cond
    (isa it NonMacro) (dq_push_tail in (. it v))
    (isa it Quote) (yield (. it v))
    (vector? it) (yield (vec (eval_sans_macros (dq it))))
    (isa it KSymbol)
      (dl d <- (@defs it)
        (if d
          (if (d :autocall)
            (if (= (d :arity) :infix1)
              (get_n_and_run out in esm_mod 1 (fn [out r _ _] (yield (apply (d :val) (dq_pop out) r))))
              (get_n_and_run out in esm_mod (d :arity) (fn [out r _ _] (yield (apply (d :val) r))))
              )
            (yield (d :val it)))
          (dl (println "could not resolve symbol:" (str it)) (yield it))
          )
        )
    :else (yield it)
    )))
(defn expand_foundation_macros ([in] (expand_foundation_macros (dq) in)) ([^Deque out ^Deque in] (while (not (dq_empty in)) (efm_mod out in)) out) )
(defn discard_whitespace       ([in] (discard_whitespace       (dq) in)) ([^Deque out ^Deque in] (while (not (dq_empty in)) (dw_some out in)) out) )
(defn expand_normal_macros     ([in] (expand_normal_macros     (dq) in)) ([^Deque out ^Deque in] (while (not (dq_empty in)) (enm_mod out in)) out) )
(defn eval_sans_macros         ([in] (eval_sans_macros         (dq) in)) ([^Deque out ^Deque in] (while (not (dq_empty in)) (esm_mod out in)) out) )


/*
  defn eval(form):
    form = macroexpand(form)
    if form isa Barrier: return VOID
    if form !isa Symbol: return form
    if form == 'nil: return nil
    if form == 'true: return Boolean.TRUE
    if form == 'false: return Boolean.FALSE
    if !form.ns: ;// ns-qualified syms are always Vars
      if b := referenceLocal(form): return new LocalBindingExpr(b tagOf(form)).eval()
    else:
      if !namespaceFor(form) && c := HostExpr.maybeClass((symbol form.ns) false):
        if Reflector.getField(c form.name true):
          return new StaticFieldExpr(@Compiler.LINE c form.name tagOf(form)).eval()
        else:
          throw ex("Unable to find static field: " + form.name + " in " + c)
    o :=
      if form.ns:
        ns := namespaceFor(currentNS() form) ||
          throw (ex "No such namespace: "form.ns)
        v := ns.findInternedVar((symbol form.name) ||
          throw (ex "No such var: "form)
        if v.ns !is currentNS() && !v.isPublic(): (warn "var: "form" is not public")
        v
      else if form.name[1:].contains(".") || form.name[0] == '[': RT.classForName(form.name)
      else
        form == @Compiler.COMPILE_STUB_SYM?
          @Compiler.COMPILE_STUB_CLASS
          currentNS().getMapping(form) ||
            throw ex("Unable to resolve symbol: "form" in this context")
    if o isa Class: return o
    if isMacro(o): throw (ex "Can't take value of a macro: "o)
    registerVar(o)
    new VarExpr(o, tagOf(form)).eval()
  /*
  defn tagOf(o):
    tag := ((meta o) :tag)
    tag isa Symbol? tag :
    tag isa String? (symbol tag) :
      nil
  */
  defn macroexpand(form): while t := macroexpand1(form) is-not form: form = t
  defn macroexpand1(form):
    if form isa ISeq && !isSpecial(op := form[0]):
      if v := isMacro(op):
        try: return v.applyTo(RT.cons(form, RT.cons(@Compiler.LOCAL_ENV, form.next())))
        catch (ArityException e): throw new ArityException(e.actual - 2, e.name) ; hide the 2 extra params for a macro
      if op isa Symbol:
        if op.ns && !namespaceFor(op): ; namesStaticMember
          if c := HostExpr.maybeClass((symbol op.ns) false):
            return preserveTag(form, RT.listStar((quote .) Symbol.intern(op.ns) Symbol.intern(op.name) form.next()))
        else:
          if op.name.endsWith("."):
            return RT.listStar(NEW, Symbol.intern(op.name[:-1]), form.next())
    form
*/