package org.jeecg.modules.automl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.automl.dto.AutomlMarketplaceAlgorithm;

import java.util.List;

/**
 * 算法市场 Service
 */
public interface IAutomlMarketplaceService {

    /**
     * 分页查询算法
     */
    IPage<AutomlMarketplaceAlgorithm> page(Integer pageNo, Integer pageSize, String keyword, String category, String taskType);

    /**
     * 根据ID查询算法
     */
    AutomlMarketplaceAlgorithm queryById(String id);

    /**
     * 查询场景分类
     */
    List<String> categories();
}
