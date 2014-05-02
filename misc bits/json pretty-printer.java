static void writeJSON(Object file, Object json) {write(file, prettyPrintJSON(json,150));}
static String prettyPrintJSON(Object json, int width) {val r = new StringBuilder(); prettyPrintJSON(json,r,0,width,""); return r+"";}
static void prettyPrintJSON(Object json, StringBuilder r, int line, int width, String indent) {
	// width of outputted lines is approximate and may vary
	String a =
		json instanceof Double?
			((Double)json).isInfinite() || ((Double)json).isNaN()?
				"null" : String.format("%.2f",json) : //! so hacky
		json instanceof Float?
			((Float)json).isInfinite() || ((Float)json).isNaN()?
				"null" : json+"" :
		json instanceof Number || json instanceof Boolean?
			json+"" :
		json instanceof String?
			"\""+JSONValue.escape(json+"")+"\"" :
		null;
	if (a != null) {
		r.append(a);
	} else if (json == null) {
		r.append("null");
	//} else if (json instanceof JSON0) {
	//	prettyPrintJSON(((JSON0)json).toJSON(),r,line,width,indent);
	} else if (json instanceof Map) {
		boolean first = true;
		val t = new StringBuilder();
		t.append("{");
		for (Map.Entry<Object,Object> v : ((Map<Object,Object>)json).entrySet()) {
			if (first) first = false;
			else t.append(", ");
			t.append(prettyPrintJSON(v.getKey()+"",0));
			t.append(": ");
			t.append(prettyPrintJSON(v.getValue(),0));
		}
		t.append("}");
		if (width == 0 || line + t.length() < width) {
			r.append(t);
		} else {
			first = true;
			r.append("{\n");
			for (Map.Entry<Object,Object> v : ((Map<Object,Object>)json).entrySet()) {
				if (first) first = false;
				else r.append(",\n");
				r.append(indent).append("\t");
				String k = prettyPrintJSON(v.getKey()+"",0);
				r.append(k).append(": ");
				int tt = (indent.length()+1)*4 + k.length() + 2;
				prettyPrintJSON(v.getValue(),r,tt,width,indent+"\t");
			}
			r.append("\n").append(indent).append("}");
		}
	} else if (json instanceof List) {
		boolean first = true;
		val t = new StringBuilder();
		t.append("[");
		for (Object v : (List)json) {
			if (first) first = false;
			else t.append(", ");
			t.append(prettyPrintJSON(v,0));
		}
		t.append("]");
		if (width == 0 || line + t.length() < width) {
			r.append(t);
		} else {
			r.append("[\n");
			first = true;
			for (Object v : (List)json) {
				if (first) first = false;
				else r.append(",\n");
				r.append(indent).append("\t");
				int tt = (indent.length()+1)*4;
				prettyPrintJSON(v,r,tt,width,indent+"\t");
			}
			r.append("\n").append(indent).append("]");
		}
	//} else if (json instanceof JSONAware) {
	//	r.append(((JSONAware)json).toJSONString());
	} else if (json instanceof D2) { //! what a hack :(
		val v = (D2)json;
		prettyPrintJSON(list(v.x,v.y),r,line,width,indent);
	} else {
		fail(json.getClass(),json);
		// consider linkedhashmap for autopickling
	//	try {
	//		Map o = hashMap();
	//		for (Field j : json.getClass().getDeclaredFields())
	//			o.put(camelToSeparate(j.getName()),j.get(json));
	//		prettyPrintJSON(o,r,line,width,indent);
	//	} catch (Exception e) {throw_(e);}
	}
	}