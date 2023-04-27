package com.tuacy.mybatis.interceptor.entity.vo;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分页返回通用Vo
 */
@Data
@Accessors(chain = true)
public class PageVO<T> {

    /**
     * 当前页码
     */
    private long current;

    /**
     * 当页展示条数
     */
    private long size;

    /**
     * 总条数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 列表数据
     */
    private List<T> records = Collections.emptyList();

    public PageVO() {
        this.current = 1;
        this.size = 10;
    }

    public PageVO(long current , long size) {
        this.current = current;
        this.size = size;
    }
}
