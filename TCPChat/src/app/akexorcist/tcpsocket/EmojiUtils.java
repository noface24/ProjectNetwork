package app.akexorcist.tcpsocket;

public class EmojiUtils {
	
	public static String convertTag(String str){
		return str
		.replaceAll("<", "&lt;")
		.replaceAll("%1%","<img src=\"a\"/>")
		.replaceAll("%2%","<img src=\"b\"/>")
		.replaceAll("%3%","<img src=\"c\"/>")
		.replaceAll("%4%","<img src=\"d\"/>")
		.replaceAll("%5%","<img src=\"e\"/>")
		.replaceAll("%6%","<img src=\"f\"/>")
		.replaceAll("%7%","<img src=\"g\"/>")
		.replaceAll("%8%","<img src=\"h\"/>")
		.replaceAll("%9%","<img src=\"j\"/>")
		.replaceAll("%10%","<img src=\"k\"/>")
		.replaceAll("%11%","<img src=\"l\"/>")
		.replaceAll("%12%","<img src=\"m\"/>")
		.replaceAll("%13%","<img src=\"n\"/>")
		.replaceAll("%14%","<img src=\"o\"/>")
		.replaceAll("%15%","<img src=\"p\"/>")
		.replaceAll("%16%","<img src=\"q\"/>")
		.replaceAll("%17%","<img src=\"r\"/>")
		.replaceAll("%18%","<img src=\"s\"/>")
		.replaceAll("%19%","<img src=\"t\"/>")
		.replaceAll("%20%","<img src=\"u\"/>")
		.replaceAll("%21%","<img src=\"v\"/>")
		.replaceAll("%22%","<img src=\"w\"/>")
		.replaceAll("%23%","<img src=\"x\"/>")
		.replaceAll("%24%","<img src=\"y\"/>")
		.replaceAll("%25%","<img src=\"z\"/>")
		.replaceAll("%26%","<img src=\"aa\"/>")
		.replaceAll("%27%","<img src=\"bb\"/>")
		.replaceAll("%28%","<img src=\"cc\"/>")
		.replaceAll("%29%","<img src=\"dd\"/>")
		.replaceAll("%30%","<img src=\"ee\"/>")
		.replaceAll("%31%","<img src=\"ff\"/>")
		.replaceAll("%32%","<img src=\"gg\"/>")
		.replaceAll("%33%","<img src=\"hh\"/>")
		.replaceAll("%34%","<img src=\"ii\"/>")
		.replaceAll("%35%","<img src=\"jj\"/>")
		.replaceAll("%36%","<img src=\"kk\"/>")
		.replaceAll("%37%","<img src=\"ll\"/>")
		.replaceAll("%38%","<img src=\"mm\"/>")
		.replaceAll("%39%","<img src=\"nn\"/>")
		.replaceAll("%40%","<img src=\"oo\"/>")

		;
	}
}