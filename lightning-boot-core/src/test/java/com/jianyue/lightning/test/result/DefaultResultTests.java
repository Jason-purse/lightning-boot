package com.jianyue.lightning.test.result;

import com.jianyue.lightning.result.DefaultResultImpl;
import com.jianyue.lightning.result.Result;
import com.jianyue.lightning.util.JsonUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Sun.
 */
public class DefaultResultTests {
    @Test
    public void test() {

        Result<Void> success = Result.success(200, "12312");
        String s = JsonUtil.getDefaultJsonUtil().asJSON(success);

        DefaultResultImpl defaultResult = JsonUtil.getDefaultJsonUtil().fromJson(s, DefaultResultImpl.class);
        System.out.println(defaultResult);
    }
}
