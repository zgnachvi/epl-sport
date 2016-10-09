package rss;

import org.junit.Assert;
import org.junit.Test;

public class URLregularExpressionTest {
    @Test
    public void test(){
        String reg = "^(http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        Assert.assertTrue("http://golazogoal.com/feed/".matches(reg));

        Assert.assertFalse("golazogoal.com/feed/".matches(reg));
    }
}
