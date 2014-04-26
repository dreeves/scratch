////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
/////  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  ///// cute-gen/main.js
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
// greenspun's-law style
function overload(){var fns = dict_by(m('length'),arguments); return function(){return fns[arguments.length].apply(this,arguments)}}
function bind(root,member){return root[member].bind(root)}
function argslice(args,i){return Array.prototype.slice.apply(args).slice(i)}
function m(m){var args = argslice(arguments,1); // m('member') is like .member
	return args.length === 0? function(v){var r = v[m]; return r instanceof Function? r.call(v) : r}
	:      args.length === 1? function(v){var r = v[m]; return r instanceof Function? r.call(v,args[0]) : (v[m]=args[0])}
	:                        function(v){return v[m].apply(v,args)}}
Function.prototype.cmp = function(f){var t = this; return function(){return t.call(this,f.apply(this,arguments))}}
function not(v){return !v}
function is(a,b){return a === b}
var def = function(f){this[f.name] = f}.bind(this)
function dict_by(f,sq){var r = {}; for(var i=0;i<sq.length;i++) r[f(sq[i])] = sq[i]; return r}

function isa(clas){return function(v){return v instanceof clas}}

// misc utils
var print = bind(console,'log')
var rand = Math.random
function sum(v){var r = 0; for (var i=0;i<v.length;i++) r += v[i]; return r}
function putE(a,b){for (v in b) a[v]=b[v]; return a} // would be 'put=' if it could be
function sign(v){return v? (v < 0? -1 : 1) : 0}

// mathy utils
function polar(r,t){return [r*Math.cos(t), r*Math.sin(t)]}
var TAU = Math.PI*2
function coercing_arith_v(f){return function(v){
	if (v instanceof Array) {var r = []; for (var i=0;i<this.length;i++) r.push(f(this[i],v[i])); return r}
	else return this.map(function(w){return f(w,v)})}}
putE(Array.prototype,{
	add:coercing_arith_v(function(a,b){return a+b}),
	sub:coercing_arith_v(function(a,b){return a-b}),
	mul:coercing_arith_v(function(a,b){return a*b}),
	div:coercing_arith_v(function(a,b){return a/b}),
	mod:coercing_arith_v(function(a,b){return a%b}),
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

var clas = overload(
	function(ctor,body){putE(ctor.prototype,body); return ctor},
	function(super_,ctor,body){
		ctor.prototype.__proto__ = super_.prototype
		ctor.super_ = super_
		ctor.prototype.super_ = function(){arguments.callee.caller.super_.apply(this,arguments)}
		return clas(ctor,body)})
function fcat(){var fns = arguments; return function(){for (var i=0;i<fns.length;i++) fns[i].apply(this,arguments)}}

var canvas = document.getElementById('canvas')
canvas.size = function(){return [canvas.width,canvas.height]}
window.onresize = function(e){
	canvas.width = window.innerWidth
	canvas.height = window.innerHeight}
window.onresize(null)

var keys_down = {}
addEventListener('keydown',function(e){keys_down[e.keyCode] = true},false)
addEventListener('keyup'  ,function(e){delete keys_down[e.keyCode]},false)

function enhance_ctx(c){return putE(c,{
	fill:overload(c.fill,function(style){
		this.fillStyle = style
		this.fill()}),
	stroke:overload(c.stroke,function(style,width){
		this.strokeStyle = style
		this.lineWidth = width
		this.stroke()}),
	draw_line:function(a,b){
		this.beginPath()
		this.moveTo(a[0]+0.5,a[1]+0.5)
		this.lineTo(b[0]+0.5,b[1]+0.5)
		this.stroke()}
	})}

function rand_pos(){return canvas.size().mul([rand(),rand()])}
function imagef(v){ // (v[,X,Y])
	var img; {var t = putE(new Image(),{src:'images/'+v+'.png', onload:function(){img = t}})}
	var XY = argslice(arguments,1)
	return XY.length === 2?
		function(c){if (img) c.drawImage(img,0,0,XY[0],XY[1])} :
		function(c){if (img) c.drawImage(img,0,0)            } }
function pos_trans(f){return function(c){c.save(); c.translate(this.x(),this.y()); f.call(this,c); c.restore()}}

var sprites = []
var agents = []
function draw_sprites (c    ){(sprites=sprites.filter(not.cmp(m('dead')))).map(m('draw'  ,c    ))}
function update_agents(delta){(agents =agents .filter(not.cmp(m('dead')))).map(m('update',delta))}

def(clas(function pos(pos){this.pos = pos},{
		x:function(){return this.pos[0]},
		y:function(){return this.pos[1]},
	}))
def(clas(pos,function agent(pos,hp,update,die){
	this.super_(pos)
	this.hp = hp
	this.die = die
	this.update = function(delta){
		if (!(0 <= this.pos[0] && this.pos[0] < canvas.size()[0] && 0 <= this.pos[1] && this.pos[1] < canvas.size()[1]))
			this.hurt(delta*100)
		if (!this.dead) update.call(this,delta)
		if (!this.dead) {
			this.vel = this.vel.mul(0.96)
			this.vel = this.vel.add(agents.filter(function(v){return this.pos.sub(v.pos).abs() < 60}.bind(this))
				.map(function(v){return this.pos.sub(v.pos).norm().mul(5*delta)}.bind(this)).sum())
			this.vel = this.vel.add(this.pos.mod(64).add(-32).sign().mul(0.5*delta))
			this.pos = this.pos.add(this.vel.mul(1000*delta))
			}}
	this.vel = [0,0]

	sprites.push(this)
	agents.push(this)
	},{
		hurt:function(v){this.hp -= v; if (this.hp <= 0 && !this.dead) {this.dead = true; this.die()}},
	}))
def(clas(agent,function food  (pos){this.super_(pos,1,function(delta){},function(){new food  (rand_pos())})},{draw:pos_trans(imagef('food'  ,64,64))}))
def(clas(agent,function weapon(pos){this.super_(pos,1,function(delta){},function(){new weapon(rand_pos())})},{draw:pos_trans(imagef('weapon',64,64))}))
def(clas(agent,function station(pos){
	this.super_(pos,20,function(delta){
		agents.filter(isa(monster)).filter(function(v){return this.pos.sub(v.pos).abs() < 200}.bind(this)).map(function(v){[v,this].map(m('hurt',100*delta))})
		},function(){new station(rand_pos())})
	},{
		draw:pos_trans(fcat(
			function(c){
				c.beginPath()
				c.arc(32,32,200,0,TAU)
				c.fill('rgba(255,170,0,.2)')
				c.stroke('rgba(0,0,0,.5)',1)},
			imagef('station',64,64))),
	}))
def(clas(agent,function monster(pos){
	this.super_(pos,10,
		function(delta){
			this.facing += (rand()-.5)*0.01*delta
			this.vel = this.vel.add(polar(1*delta,this.facing))
			agents.filter(isa(hero)).map(function(v){
				this.vel = this.vel.add(this.pos.sub(v.pos).norm().mul(-0.5*delta))
				if (this.pos.sub(v.pos).abs() < 64) {this.hurt(10*delta); v.hurt(10*delta)}
				}.bind(this))
			},
		function(){new monster(rand_pos())})
	this.facing = rand()*TAU
	},{
		draw:pos_trans(imagef('monster'))
	}))
def(clas(agent,function hero(pos){
	this.super_(pos,100,function(delta){
		this.vel=this.vel.add([(keys_down[37]?-1:0)+(keys_down[39]?1:0), (keys_down[38]?-1:0)+(keys_down[40]?1:0)].mul(3*delta))
		// CURRENT: make this cooler
		this.hp += agents.filter(isa(station)).filter(function(v){return this.pos.sub(v.pos).abs() < 200}.bind(this)).map(function(v){
			var t = 5*delta; v.hurt(t); return t}).sum()
		this.hp=Math.round(this.hp*10000)/10000
		},function(){hero_ = new hero(rand_pos())})
	},{
		draw:pos_trans(imagef('hero'))
	}))

sprites.push({draw:imagef('background2')})
sprites.push({draw:function(c){
	c.strokeStyle = 'rgba(0,0,0,.3)'
	c.lineWidth = 1
	for (var x = 32; x < canvas.width ; x += 64) c.draw_line([x,0],[x,canvas.height])
	for (var y = 32; y < canvas.height; y += 64) c.draw_line([0,y],[canvas.width,y ])
	}})
sprites.push({draw:function(c){
	c.fillStyle = 'white'
	c.font = '24px Consolas'
	c.textBaseline = 'top'
	c.fillText('sprites: '+sprites.length,32,32)
	c.fillText('hero.hp: '+agents.filter(isa(hero)).map(m('hp')),32,64)
	}})

for (var i=0;i<4;i++) new food(rand_pos())
for (var i=0;i<4;i++) new weapon(rand_pos())
for (var i=0;i<10;i++) new station(rand_pos())
for (var i=0;i<4;i++) new monster(rand_pos())
var hero_ = new hero(canvas.size().div(2))

{	var prev = Date.now()
	var ctx = enhance_ctx(canvas.getContext('2d'))
	setInterval(function(){
		var now = Date.now()
		update_agents((now - prev)/1000)
		draw_sprites(ctx)
		prev = now
		},10)}