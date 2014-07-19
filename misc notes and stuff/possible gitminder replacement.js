// here are the possibly-useful parts from a possible gitminder replacement

function github(path,f){request(path,{},{'Authorization':'token '+auth.github,'User-Agent':user},f,'https://api.github.com')}
function github_all_pages(path,f){var r = []; github(path,function λ(v,response){r = r.concat(v); var t = header_links(response.headers.link).next; if (t) github(t,λ); else f(r,response)})}
function github_all_commits(f) {
	github('/users/'+user+'/repos',function λ(v,response){
		var r = {}
		var repos = v.map(function(v){return v.name})
		repos.forEach(function(v,j){github_all_pages('/repos/'+user+'/'+v+'/commits',function(v){r[j] = v; if (_.range(0,repos.length).every(function(v){return r[v]})) {var t = []; for (var i=0;i<repos.length;i++) t = t.concat(r[i]); f(t)}})})
	}) }

github_all_commits(function(v){
	var dates = v.filter(function(v){return v.committer.login === user}).map(function(v){return v.commit.author.date}).sort()
	var dates = frequencies(dates.map(function(v){return new Date(v).hours(-3).yyyy_mm_dd()}))
	beeminder_get(function(v){
		// bee warned, arbitrary hacks lie here
		var beedays = v.filter(function(v){return v.comment !== 'temp'}).map(function(v){return [new Date(v.timestamp*1000).yyyy_mm_dd(), parseInt(v.value), v.id]})
		var beedict = dict_by(beedays,function(v){return v[0]})
		Object.keys(dates).map(function(k){if (!beedict[k] || beedict[k][1]!==dates[k]) {
			var v = {timestamp:new Date(k).hours(12)/1000,value:dates[k],comment:'auto-entered by aligitminder'}
			print('adding',new Date(v.timestamp*1000).yyyy_mm_dd(),v.value,beedict[k])
			if (!dry_run) beeminder_add(v)}})
		beedays.map(function(v){if (v[1] !== dates[v[0]]) {print('deleting',v); if (!dry_run) beeminder_delete(v[2])}})
	})
})