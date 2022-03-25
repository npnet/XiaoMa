package com.xiaoma.pet.common.callback;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/4/2 0002 17:28
 *       desc：文件查找
 * </pre>
 */
public interface OnFindXmlTagCallback {
    
    /**
     * 找到标签对应的file path
     *
     * @param path 文件路劲
     */
    void findPath(String path);


    /**
     * 查找失败
     */
    void failure(String msg);
}
