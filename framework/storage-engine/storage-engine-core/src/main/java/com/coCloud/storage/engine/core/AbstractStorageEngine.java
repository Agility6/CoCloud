package com.coCloud.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.coCloud.cache.core.constants.CacheConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.coCloud.storage.engine.core.context.StoreFileContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.util.Objects;

/**
 * ClassName: AbstractStorageEngine
 * Description: 文件存储引擎模块公用抽象类
 * <p>
 * 具体的文件存储实现方案的公用逻辑需要抽离到该类中
 *
 * @Author agility6
 * @Create 2024/5/20 20:29
 * @Version: 1.0
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new CoCloudBusinessException("具体的缓存实现需要引用到项目中");
        }
        return cacheManager.getCache(CacheConstants.CO_CLOUD_CACHE_NAME);
    }

    /**
     * 存储物理文件
     * <p>
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 校验上传物理文件的上下文信息
     *
     * @param context
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件的总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

    /**
     * 删除物理文件
     * <p>
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }


    /**
     * 执行删除物理文件的动作
     * 下沉到子类去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    /**
     * 校验删除物理文件的上下文信息
     * @param context
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }


}
