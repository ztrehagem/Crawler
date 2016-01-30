package crawler2;

import java.util.ArrayList;
import java.util.List;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLModifier {

	private final Brain				brain;
	private final String			url;
	private final int				h;
	private final OutputDocument	od;

	HTMLModifier( Brain brain, String url, String src, int h ) {
		this.brain = brain;
		this.url = url;
		this.h = h;
		this.od = new OutputDocument( new Source( src.replace( "\n", "" ) ) );

		this.exec();
	}

	private void exec() {
		final List<AttributeModifier> list = new ArrayList<>();

		list.add( new FileLinkTagAttributeModifier( brain, od, url, HTMLElementName.IMG, "src" ) );
		list.add( new FileLinkTagAttributeModifier( brain, od, url, HTMLElementName.LINK, "href" ) );
		list.add( new FileLinkTagAttributeModifier( brain, od, url, HTMLElementName.SCRIPT, "src" ) );
		list.add( new FileLinkTagAttributeModifier( brain, od, url, HTMLElementName.VIDEO, "src" ) );
		// TODO 追加　他のメディアタグ

		list.add( new PageLinkTagAttributeModifier( brain, od, url, HTMLElementName.A, "href", h ) );
		list.add( new PageLinkTagAttributeModifier( brain, od, url, HTMLElementName.IFRAME, "src", h ) );

		list.add( new SpecificAttributeModifier( brain, od, url, "background" ) );

		list.add( new StyleAttributeModifier( brain, od, url ) );

		list.add( new StyleTagContentModifier( brain, od, url ) );

		for( AttributeModifier am : list )
			am.modify();

	}

	String getResult() {
		return this.od.toString();
	}
}
