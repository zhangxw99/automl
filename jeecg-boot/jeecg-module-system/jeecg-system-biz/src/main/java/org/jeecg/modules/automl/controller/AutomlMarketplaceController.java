package org.jeecg.modules.automl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.automl.dto.AutomlMarketplaceAlgorithm;
import org.jeecg.modules.automl.service.IAutomlMarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 算法市场
 */
@Tag(name = "算法市场")
@RestController
@RequestMapping("/automl/marketplace")
public class AutomlMarketplaceController {

    @Autowired
    private IAutomlMarketplaceService automlMarketplaceService;

    /**
     * 分页查询算法
     */
    @AutoLog(value = "算法市场-分页查询算法")
    @Operation(summary = "算法市场-分页查询算法")
    @GetMapping(value = "/algorithms/list")
    public Result<IPage<AutomlMarketplaceAlgorithm>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          @RequestParam(name = "keyword", required = false) String keyword,
                                                          @RequestParam(name = "category", required = false) String category,
                                                          @RequestParam(name = "taskType", required = false) String taskType) {
        return Result.ok(automlMarketplaceService.page(pageNo, pageSize, keyword, category, taskType));
    }

    /**
     * 通过id查询算法
     */
    @AutoLog(value = "算法市场-通过id查询算法")
    @Operation(summary = "算法市场-通过id查询算法")
    @GetMapping(value = "/algorithms/queryById")
    public Result<AutomlMarketplaceAlgorithm> queryById(@RequestParam(name = "id") String id) {
        AutomlMarketplaceAlgorithm algorithm = automlMarketplaceService.queryById(id);
        if (algorithm == null) {
            return Result.error("未找到对应算法");
        }
        return Result.ok(algorithm);
    }

    /**
     * 查询算法场景分类
     */
    @AutoLog(value = "算法市场-查询算法场景分类")
    @Operation(summary = "算法市场-查询算法场景分类")
    @GetMapping(value = "/algorithms/categories")
    public Result<List<String>> categories() {
        return Result.ok(automlMarketplaceService.categories());
    }
}
