package crawler;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class CssExternal {

	private final URL					url;
	private final File					file;
	private final String				prefix;
	private final Map<String, String>	extfilemap;

	CssExternal( URL url, File file ) {
		this.url = url;
		this.file = file;
		this.prefix = "cssext-" + file.getName() + "-";
		this.extfilemap = new HashMap<>();
	}

	void process() {
		// url(".*");の検索
		// fileIdを取得、書き換え、Mapに追加
		// MapからExternalを生成して走らせる
	}
}
