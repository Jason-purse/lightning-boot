package com.jianyue.lightning.boot.starter.exception.dao;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:32
 * @Description 默认的CRUD 异常
 */
public class DefaultCrudException extends DefaultDataOperationException {

    private DefaultCrudException(String title, String content, String operationMethod) {
        super(title + content, operationMethod);
    }

    /**
     * 读取异常
     *
     * @param message message
     * @return DefaultCrudException
     */
    public static DefaultCrudException exceptionForRead(String title, String message) {
        return new DefaultCrudException(title, CrudOps.READ.name(), message);
    }

    /**
     * 新增异常
     *
     * @param message message
     * @return DefaultCrudException
     */
    public static DefaultCrudException exceptionForCreate(String title, String message) {
        return new DefaultCrudException(title, CrudOps.CREATE.name(), message);
    }

    /**
     * 更新异常
     *
     * @param message message
     * @return DefaultCrudException
     */
    public static DefaultCrudException exceptionForUpdate(String title, String message) {
        return new DefaultCrudException(title, CrudOps.UPDATE.name(), message);
    }

    /**
     * 删除异常
     *
     * @param message message
     * @return DefaultCrudException
     */
    public static DefaultCrudException exceptionForDelete(String title, String message) {
        return new DefaultCrudException(title, CrudOps.DELETE.name(), message);
    }


    // ----------------------------- for china --------------------------------------

    // 默认locale 版本
    public static DefaultCrudException readExForChina(String message) {
        return new DefaultCrudException("读取数据异常: ", message, CrudOps.READ.name());
    }

    public static DefaultCrudException readNoDataExForChina() {
        return readExForChina("没有发现对应数据 !");
    }

    // 默认locale 版本
    public static DefaultCrudException createExForChina(String message) {
        return new DefaultCrudException("新增数据异常: ", message, CrudOps.CREATE.name());
    }

    public static DefaultCrudException createDuplicateExForChina() {
        return createExForChina("发现重复数据 !");
    }

    public static DefaultCrudException createDuplicateExForChina(Object duplicateData) {
        return createExForChina("发现重复数据为: " + duplicateData.toString());
    }

    // 默认locale 版本
    public static DefaultCrudException updateExForChina(String message) {
        return new DefaultCrudException("更新数据异常: ", message, CrudOps.READ.name());
    }

    public static DefaultCrudException updateNoDataEx() {
        return updateExForChina("没有发现对应数据,无法更新 !");
    }

    public static DefaultCrudException updateDuplicateExForChina(Object duplicateData) {
        return updateExForChina("发现重复数据为: " + duplicateData.toString());
    }

    // 默认locale 版本
    public static DefaultCrudException deleteExForChina(String message) {
        return new DefaultCrudException("删除数据异常: ", message, CrudOps.READ.name());
    }

    public static DefaultCrudException deleteNoExForChina() {
        return deleteExForChina("没有发现对应的数据,无法删除 !");
    }

    // 删除好像比较严谨,不需要对应的快捷方式提供异常处理 ...

}
