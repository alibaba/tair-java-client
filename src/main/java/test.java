import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.impl.DefaultTairManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianlong.ql on 2016/5/4.
 */
public class test extends TestCase {
	public  void test_all(){

		DefaultTairManager tm = new DefaultTairManager();
		List<String> cs = new ArrayList<String>();
		cs.add("10.101.72.136:58999");

		tm.setConfigServerList(cs);
		tm.setGroupName("group_1");
		tm.setTimeout(2000);
		tm.setCompressionThreshold(1100000);//设置压缩限制
//	    	tm.setCharset("UTF-8");
		tm.init();


		tm.put(0, "keytest111", "value1111");
		Result<DataEntry> rt =  tm.get(0, "keytest111");

		tm.delete(0, "keytest111");

		rt = tm.get(0, "keytest111");
	}
}
