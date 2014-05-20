class isa Widget {
	priv final String[] lines;
	
	ctor(String... lines) {ctor1(lines);}
	
	shy void subdraw() {
		fora(i, lines) Font.DEFAULT.drawString(lines[i], 0, i * Font.DEFAULT.sizeY);
	}
	
	shy void regen() {
		int w = 0;
		for (val s : lines) w = S.max(w, Font.DEFAULT.calcLength(s));
		X = w;
		Y = Font.DEFAULT.sizeY * lines.length;
	}
	
	//WIDGET_FLUENCY
	Text show() {super.show(); return this}
	Text hide() {super.hide(); return this}
	Text moveTo(real relx, real rely) {super.moveTo(relx, rely); return this}
	Text moveTo(real relx, real rely, int pixelx, int pixely) {super.moveTo(relx, rely, pixelx, pixely); return this}
	Text anchor(real anchorx, real anchory) {super.anchor(anchorx, anchory); return this}
	Text center() {super.center(); return this}
}