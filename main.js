////////////////////////////////////////////////////
//////////////////////////////////////////////////// everything except section main
/////  THE FOLLOWING IS COPIED FROM ELSEWHERE  ///// see: lang-alpha
//////////////////////////////////////////////////// well, not actually, but everything worth using is there
////////////////////////////////////////////////////
//===--------------------------------------------===// greenspuns //===--------------------------------------------===//

function λ(s){return eval('(function(v,w,x,y,z){return '+s+'})')}

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
var TAU = Math.PI*2
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

//===--------------------------------------------===// main //===--------------------------------------------===//

function vec2to1(v){return v[0]*1000000+v[1]}
function vec1to2(v){var t = v%1000000; return [Math.round(v/1000000), t>500000? t-1000000 : t<-500000? t+1000000 : t]}

var draw = SVG('canvas')
var width = 4
var spacing = 10
var offset = [300,150]

function my_scale(v){return v.mul(spacing).add(offset)}

function adj(v){return [[0,1],[1,0],[0,-1],[-1,0]].map(m('add',v))}

var bound = [[0,0],[0,0]]
var _space = {}; var space = overload(
	function(){var r=[]; for (v in _space) r.push(vec1to2(v)); return r},
	function(i){return _space[vec2to1(i)]},
	function(i,v){return _space[vec2to1(i)] = v},
	function(v,i,a){return space(v)})
var lines = []
function add_point(v){space(v,true); bound[0] = bound[0].min(v); bound[1] = bound[1].max(v)}
function append_point(l,v){l.push(v); add_point(v); return l}

function add_line(v){lines.push(v); v.map(add_point)}
add_line([[0,1],[0,0],[1,0]])
add_line([[1,1],[2,1],[2,2],[2,3]])
add_line([[1,-1],[0,-1],[-1,-1],[-1,0],[-1,1],[-1,2],[0,2],[1,2],[1,3]])

function rand_in_boundary(){
	var xy = [
		rand_i_i(bound[0][0]-1,bound[1][0]+1),
		rand_i_i(bound[0][1]-1,bound[1][1]+1)]
	return !space(xy) && any(adj(xy).map(space))? xy : rand_in_boundary()}

for (var i=0;i<200;i++) {
	line = append_point([],rand_in_boundary())
	for (;;) {
		var t = rand_weighted(adj(sub(line,-1)).filter(not.cmp(space)).map(function(v){
			return [{0:0,1:1,2:10,3:50,4:500}[sum(adj(v).map(function(v){return v?1:0}.cmp(space)))], v]}))
		if (t) append_point(line, t)
		if (!t || rand() < 0.2) break}
	lines.push(line)
}

lines.map(function(vs){
	vs = vs.map(my_scale)
	if (vs.length === 1) {
		vs = [vs[0].sub([0,width/2]), vs[0].add([0,width/2])]
	} else {
		// for [vs, reverse(vs)]: it[0] += (it[0] - it[1]).norm() * width/2
		vs[0] = vs[0].add(vs[0].sub(vs[1]).norm().mul(width/2))
		vs[vs.length-1] = sub(vs,-1).add(sub(vs,-1).sub(sub(vs,-2)).norm().mul(width/2))
	}
	draw.polyline(vs).fill('none').stroke({width: width})
	})