//===--------------------------------------------===// greenspuns //===--------------------------------------------===//
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
/////  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  /////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////

function overload(){var fns = dict_by(m('length'),arguments); return function(){return fns[arguments.length].apply(this,arguments)}}
function bind(root,member){return root[member].bind(root)}
function argslice(args,i){return Array.prototype.slice.apply(args).slice(i)}
function m(m){var args = argslice(arguments,1); // m('member',args) → function(v){return v.member(args)}
	return args.length === 0? function(v){var r = v[m]; return r instanceof Function? r.call(v) : r}
	:      args.length === 1? function(v){var r = v[m]; return r instanceof Function? r.call(v,args[0]) : (v[m]=args[0])}
	:                        function(v){return v[m].apply(v,args)}}
Function.prototype.cmp = function(f){var t = this; return function(){return t.call(this,f.apply(this,arguments))}}
function not(v){return !v}
function is(a,b){return a === b}
var def = function(f){this[f.name] = f}.bind(this)
function dict_by(f,sq){var r = {}; for(var i=0;i<sq.length;i++) r[f(sq[i])] = sq[i]; return r}

function isa(clas){return function(v){return v instanceof clas}} // should probably just use cmp

var sub = overload(
	function(v,i){return v[i<0? i+v.length : i]},
	function(i){return function(v){return sub(v,i)}})

//===--------------------------------------------===// misc utils //===--------------------------------------------===//

var print = bind(console,'log')
var rand = Math.random
function sum(v){var r = 0; for (var i=0;i<v.length;i++) r += v[i]; return r}
function putE(a,b){for (v in b) a[v]=b[v]; return a} // would be 'put=' if it could be
function sign(v){return v? (v < 0? -1 : 1) : 0}

function any(vs){for (var i=0;i<vs.length;i++) if (vs[i]) return true; return false}

function rand_nth(vs){return vs.length === 0? undefined : vs[Math.floor(rand()*vs.length)]}
var rand_i_i = overload(function(a,b){return Math.floor(rand()*(b+1 - a)) + a}, function(ab){return rand_i_i(ab[0],ab[1])})
var rand_i   = overload(function(a,b){return Math.floor(rand()*(b   - a)) + a}, function(ab){return rand_i  (ab[0],ab[1])})
function rand_weighted(v){
	var total = sum(v.map(sub(0)))
	if (total === 0) return undefined
	var i = rand_i(0,total)
	for (var j=0;j<v.length;j++) {
		i -= v[j][0]
		if (i < 0) return v[j][1]
	}
	throw "wut"}

//===--------------------------------------------===// misc mathy utils //===--------------------------------------------===//

var min = Math.min
var max = Math.max
Math.TAU = Math.PI*2
function polar(r,t){return [r*Math.cos(t), r*Math.sin(t)]}

function coercing_arith_v(f){return function(v){
	if (v instanceof Array) {var r = []; for (var i=0;i<this.length;i++) r.push(f(this[i],v[i])); return r}
	else return this.map(function(w){return f(w,v)})}}
putE(Array.prototype,{
	add:coercing_arith_v(function(a,b){return a+b}),
	sub:coercing_arith_v(function(a,b){return a-b}),
	mul:coercing_arith_v(function(a,b){return a*b}),
	div:coercing_arith_v(function(a,b){return a/b}),
	mod:coercing_arith_v(function(a,b){return a%b}),
	min:coercing_arith_v(function(a,b){return min(a,b)}),
	max:coercing_arith_v(function(a,b){return max(a,b)}),
	abs:function(){return Math.sqrt(sum(this.mul(this)))},
	sum:function(){
		if (this.length === 0) return 0
		var r = this[0]
		if (r instanceof Array) for (var i=1;i<this.length;i++) r = r.add(this[i])
		else                    for (var i=1;i<this.length;i++) r += this[i]
		return r},
	sign:function(){return this.map(sign)},
	norm:function(){var t = this.abs(); return t === 0? this : this.div(t)},
	})

////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
//////////////////  END DUPLICATE SECTION  /////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
//===--------------------------------------------===// main //===--------------------------------------------===//

var high_letters = {}
'aᵃ bᵇ cᶜ dᵈ eᵉ fᶠ gᵍ hʰ iⁱ jʲ kᵏ lˡ mᵐ nⁿ oᵒ pᵖ qq rʳ sˢ tᵗ uᵘ vᵛ wʷ xˣ yʸ zᶻ Aᴬ Bᴮ Cᶜ Dᴰ Eᴱ Fᶠ Gᴳ Hᴴ Iᴵ Jᴶ Kᴷ Lᴸ Mᴹ Nᴺ Oᴼ Pᴾ QQ Rᴿ Sˢ Tᵀ Uᵁ Vⱽ Wᵂ Xˣ Yʸ Zᶻ 0⁰ 1¹ 2² 3³ 4⁴ 5⁵ 6⁶ 7⁷ 8⁸ 9⁹ +⁺ -⁻ =⁼ (⁽ )⁾ Αᴬ αᵅ Βᴮ βᵝ Γᵞ γᵞ Δᵟ δᵟ Εᴱ εᵋ Ζᶻ ζᶻ Ηᴴ ηʰ Θᶿ θᶿ Ιᴵ ιᶥ Κᴷ κᵏ Λᴸ λˡ Μᴹ μᵐ Νᴺ νⁿ Ξˣ ξˣ Οᴼ οᵒ Πᴾ πᵖ Ρᴿ ρᴿ Σˢ σˢ Τᵀ τᵗ Υᵁ υᵘ Φᶲ φᶲ Χᵡ χᵡ Ψᵠ ψᵠ Ωᴼ ωᴼ _-'
	.split(' ').map(function(v){high_letters[v[0]] = v[1]})
'!@#$%^&*`/\\|[]{}"\'<>?'
	.split('').map(function(v){high_letters[v] = v})
var low_letters = {}
'aₐ bᵦ eₑ hₕ iᵢ jⱼ kₖ lₗ mₘ nₙ oₒ pₚ rᵣ sₛ tₜ uᵤ vᵥ xₓ yᵧ Aₐ Bᵦ Eₑ Hₕ Iᵢ Jⱼ Kₖ Lₗ Mₘ Nₙ Oₒ Pₚ Rᵣ Sₛ Tₜ Uᵤ Vᵥ Xₓ Yᵧ 0₀ 1₁ 2₂ 3₃ 4₄ 5₅ 6₆ 7₇ 8₈ 9₉ +₊ -₋ =₌ (₍ )₎ Αₐ αₐ Βᵦ βᵦ Γᵧ γᵧ Εₑ εₑ Ηₕ ηₕ Θₜ θₜ Ιᵢ ιᵢ Κₖ κₖ Λₗ λₗ Μₘ μₘ Νₙ νₙ Ξₓ ξₓ Οₒ οₒ Πₚ πₚ Ρᵨ ρᵨ Σₛ σₛ Τₜ τₜ Υᵤ υᵤ Χᵪ χᵪ Ψᵩ ψᵩ Ωₒ ωₒ __'
	.split(' ').map(function(v){low_letters[v[0]] = v[1]})
',.~:;'
	.split('').map(function(v){low_letters[v] = v})

// todo:
// shiny on autocopy
// github page
// http://eeemo.net/ ?

function zalgify_textarea() {document.f.zalgy.value = zalgify(document.f.original.value)}

function zalgify(v) {
	var first = high_letters
	return v.split('').map(function(v){
		var second = first === high_letters? low_letters : high_letters
		var t = first[v]; if (t) {first = second; return t} else return second[v] || v
		}).join('')}