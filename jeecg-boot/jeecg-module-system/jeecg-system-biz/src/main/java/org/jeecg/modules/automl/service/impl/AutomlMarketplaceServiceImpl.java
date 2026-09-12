package org.jeecg.modules.automl.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.automl.dto.AutomlMarketplaceAlgorithm;
import org.jeecg.modules.automl.service.IAutomlMarketplaceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * SenseMu 算法市场只读目录。
 *
 * <p>cs.sensemu.com 当前开启 SENSEMU_PREVIEW_MODE，公开算法来自
 * apps/web/lib/catalog-mock-data.ts。这里将该演示目录随包发布，保持后端查询稳定。</p>
 */
@Service
public class AutomlMarketplaceServiceImpl implements IAutomlMarketplaceService {

    private final List<AutomlMarketplaceAlgorithm> algorithms;

    public AutomlMarketplaceServiceImpl(ObjectMapper objectMapper) {
        try (InputStream inputStream = getClass().getResourceAsStream("/marketplace/automl-marketplace-algorithms.json")) {
            if (inputStream == null) {
                throw new JeecgBootException("算法市场数据不存在");
            }
            this.algorithms = List.copyOf(objectMapper.readValue(inputStream, new TypeReference<List<AutomlMarketplaceAlgorithm>>() {
            }));
        } catch (IOException e) {
            throw new JeecgBootException("算法市场数据加载失败");
        }
    }

    @Override
    public IPage<AutomlMarketplaceAlgorithm> page(Integer pageNo, Integer pageSize, String keyword, String category, String taskType) {
        int current = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;

        List<AutomlMarketplaceAlgorithm> filtered = algorithms.stream()
                .filter(item -> matches(item, keyword, category, taskType))
                .toList();

        int fromIndex = Math.min((current - 1) * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        Page<AutomlMarketplaceAlgorithm> page = new Page<>(current, size);
        page.setRecords(filtered.subList(fromIndex, toIndex));
        page.setTotal(filtered.size());
        return page;
    }

    @Override
    public AutomlMarketplaceAlgorithm queryById(String id) {
        return algorithms.stream()
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> categories() {
        return algorithms.stream()
                .map(AutomlMarketplaceAlgorithm::getCategory)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }

    private boolean matches(AutomlMarketplaceAlgorithm item, String keyword, String category, String taskType) {
        if (StringUtils.hasText(category) && !category.equals(item.getCategory())) {
            return false;
        }
        if (StringUtils.hasText(taskType) && !taskType.equals(item.getTaskType())) {
            return false;
        }
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return searchableText(item).contains(normalizedKeyword);
    }

    private String searchableText(AutomlMarketplaceAlgorithm item) {
        return (item.getTitle() + " "
                + item.getSummary() + " "
                + item.getProviderName() + " "
                + item.getCategory() + " "
                + item.getModelArchitecture() + " "
                + item.getInputSize() + " "
                + String.join(" ", item.getCapabilityVerifiedScenes()) + " "
                + String.join(" ", item.getClasses())).toLowerCase(Locale.ROOT);
    }
}
