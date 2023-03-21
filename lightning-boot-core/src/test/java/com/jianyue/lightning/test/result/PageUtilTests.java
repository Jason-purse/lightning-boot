package com.jianyue.lightning.test.result;

import com.jianyue.lightning.result.PageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * PageUtil unit tests
 * @author FLJ
 */
public class PageUtilTests {
    @Test
    public void firstPageOrDefaultFill() {
        PageUtil<Object> page = PageUtil.fullEmpty(0, 0);
        Assertions.assertEquals(page.getPage(), 1);
        Assertions.assertEquals(page.getSize(), 10);

        PageUtil<Object> pageU = PageUtil.fullEmpty(1, 1);

        Assertions.assertEquals(pageU.getPage(), 1);
        Assertions.assertEquals(pageU.getSize(), 1);


    }

    @Test
    public void hasPreviousPage() {
        PageUtil<Object> empty = PageUtil.empty(1, 1, 0);
        Assertions.assertFalse(empty.isPreviousPage());

        PageUtil<Object> of = PageUtil.of(2, 0);
        Assertions.assertTrue(of.isPreviousPage());
    }

    @Test
    public void hasContent() {
        PageUtil<String> of = PageUtil.of(0, 0);
        Assertions.assertNotNull(of.getContent());

        PageUtil<String> copyOf = PageUtil.copyOf(of, 20, Arrays.asList("1", "2", "3"));

        Assertions.assertNotNull(copyOf.getContent());
        Assertions.assertFalse(copyOf.getContent().isEmpty());

    }

    @Test
    public void empty() {
        PageUtil<Object> first = PageUtil.first();
        Assertions.assertTrue(first.getContent().isEmpty());
    }

    @Test
    public void fullEmpty() {
        PageUtil<Object> page = PageUtil.fullEmpty(PageUtil.first());
        Assertions.assertEquals(0, page.getTotal());
    }

    @Test
    public void offset() {
        PageUtil<Object> first = PageUtil.first();
        Assertions.assertEquals(0, first.getOffset());

        PageUtil<Object> next = first.next();

        Assertions.assertEquals(10, next.getOffset());

        PageUtil<Object> util = PageUtil.first(20);

        Assertions.assertEquals(0, util.getOffset());
        PageUtil<Object> pageUtil = util.next();

        Assertions.assertEquals(20, pageUtil.getOffset());
    }

}
