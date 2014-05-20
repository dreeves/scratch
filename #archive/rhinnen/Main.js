/*/============================================================// info //============================================================//
arr.forEach(function(ref) {
	ref.val = Math.floor(Math.random() * 5)
})
var arr = init(newArr(20), function(i) {return i})
//*/
//============================================================// S //============================================================//

loadClass("brain_0_3")
importPackage(Packages.util)

function pln() {if (arguments.length == 0) java.lang.System.out.println(); else java.lang.System.out.println(arguments[0])}

function fill(arr, val) {
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] instanceof Array) fill(arr[i], val)
		else arr[i] = val
	}
	return arr
}

function init(arr, func) {
	for (var i = 0; i < arr.length; i++)
		arr[i] = func(i)
	return arr
}

function newArr() {
	if (arguments.length == 1) return new Array(arguments[0])
	if (arguments.length > 1) {
		var ret = new Array(arguments[0])
		var subArgs = new Array(arguments.length - 1)
		for (var i = 1; i < arguments.length; i++) subArgs[i-1] = arguments[i]
		for (var i = 0; i < arguments[0]; i++) ret[i] = newArr.apply(this, subArgs)
		return ret
	}
}

//============================================================// global 'classes' //============================================================//

function Ref() {this.val}

//============================================================// global functions //============================================================//

function parseOutput(output) {
	var ret = newArr(2)
	ret[0] = output[0] < .3? 0 : output[0] < .7? 1 : 2
	ret[1] = output[1] < .3? 0 : output[1] < .7? 1 : 2
	return ret
}

//============================================================// main //============================================================//

(function(){
	function toNN() {
		var ret = newArr(9)
		for (var x = 0; x < 3; x++)
		for (var y = 0; y < 3; y++)
			ret[x*3+y] = (grid[x][y] == "X"? .9 : grid[x][y] == "O"? .5 : .1)
		return ret
	}
	function printGrid() {
		S.pln(grid[0][0] + "|" + grid[1][0] + "|" + grid[2][0])
		S.pln("-+-+-")
		S.pln(grid[0][1] + "|" + grid[1][1] + "|" + grid[2][1])
		S.pln("-+-+-")
		S.pln(grid[0][2] + "|" + grid[1][2] + "|" + grid[2][2])
	}
	function getAImove() {
		var output = net.run(toNN())
		var move = newArr(2)
		move[0] = output[0] < .3? 0 : output[0] < .7? 1 : 2
		move[1] = output[1] < .3? 0 : output[1] < .7? 1 : 2
		if (grid[move[0]][move[1]] != " ") {
			var empties = 0
			for (var x = 0; x < 3; x++)
			for (var y = 0; y < 3; y++)
			if (grid[x][y] == " ")
				empties++
			var sample
			for (var x = 0; x < 3; x++)
			for (var y = 0; y < 3; y++)
			if (grid[x][y] == " " && (--empties) == 0)
				{sample = [(x-1)*.4+.5, (y-1)*.4+.5]; break}
			net.train({input: toNN(), output: sample})
			return getAImove()
		}
		return move
	}
	
	var grid = fill(newArr(3, 3), " ")
	
	pln("training...")
	var net = new brain.NeuralNetwork()
	net.train([
			{input: toNN(), output: [.5, .5]},
			{input: [.1, .9, .1, .1, .1, .1, .1, .1, .1], output: [.5, .5]},
			{input: [.1, .9, .1, .1, .1, .9, .1, .1, .1], output: [.5, .5]},
			{input: [.1, .9, .1, .1, .9, .9, .1, .1, .1], output: [.9, .1]},
		])
			
	printGrid()
	while (true) {
		var input = new String(S.scanner.nextLine())
		if (input == "q") break
		grid[input[0]][input[1]] = "X"
		printGrid()
		pln("thinking...")
		var ai = getAImove()
		grid[ai[0]][ai[1]] = "O"
		printGrid()
	}

	/*net.train([{input: [1, 0.65, 0],  output: {orange: 1}},
			   {input: [0, 0.54, 0],  output: {green: 1}},
			   {input: [0.6, 1, 0.5], output: {green: 1}},
			   {input: [0.67, 0, 1],  output: {purple: 1}}])
			   
	var div = 5;
	for (var r = 0; r <= div; r++) {
		for (var g = 0; g <= div; g++) {
			for (var b = 0; b <= div; b++) {
				var output = net.run([r/div, g/div, b/div])
				p(output.orange > output.green? (output.orange > output.purple? "." : " ") : (output.green > output.purple? "G" : " "))
			}
			pln("")
		}
		pln("")
	}*/
})()