package org.jeecg.modules.automl.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 算法市场商品
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "算法市场商品")
public class AutomlMarketplaceAlgorithm {

    @Schema(description = "商品ID")
    private String id;

    @Schema(description = "服务商标识")
    private String providerWorkspaceId;

    @Schema(description = "服务商名称")
    private String providerName;

    @Schema(description = "部署ID")
    private String deploymentId;

    @Schema(description = "能力规格ID")
    private String capabilitySpecId;

    @Schema(description = "能力标识")
    private String capabilitySlug;

    @Schema(description = "能力版本")
    private Integer capabilityVersionNumber;

    @Schema(description = "能力名称")
    private String capabilityDisplayName;

    @Schema(description = "问题定义")
    private String capabilityProblemDefinition;

    @Schema(description = "输出契约")
    private String capabilityOutputContract;

    @Schema(description = "已验证场景")
    private List<String> capabilityVerifiedScenes;

    @Schema(description = "不支持条件")
    private List<String> capabilityUnsupportedConditions;

    @Schema(description = "调用地址")
    private String endpointUrl;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型版本")
    private Integer modelVersionNumber;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "商品名称")
    private String title;

    @Schema(description = "商品简介")
    private String summary;

    @Schema(description = "场景分类")
    private String category;

    @Schema(description = "计费单位")
    private String pricingUnit;

    @Schema(description = "千次调用价格，单位分")
    @JsonAlias("price_per_1000_cents")
    private Integer pricePer1000Cents;

    @Schema(description = "月度额度")
    private Integer monthlyQuotaUnits;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "发布时间")
    private String publishedAt;

    @Schema(description = "订阅ID")
    private String subscriptionId;

    @Schema(description = "订阅状态")
    private String subscriptionStatus;

    @Schema(description = "剩余额度")
    private Integer remainingUnits;

    @Schema(description = "是否演示数据")
    private Boolean isMock;

    @Schema(description = "效果预览")
    private Preview preview;

    @Schema(description = "指标")
    private List<Metric> metrics;

    @Schema(description = "识别类别")
    private List<String> classes;

    @Schema(description = "模型结构")
    private String modelArchitecture;

    @Schema(description = "输入尺寸")
    private String inputSize;

    @Schema(description = "P95延迟")
    private String latencyP95;

    @Schema(description = "评估依据")
    private String evaluationBasis;

    @Schema(description = "更新时间")
    private String updatedLabel;

    /**
     * 指标
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "算法指标")
    public static class Metric {

        @Schema(description = "指标名称")
        private String label;

        @Schema(description = "指标值")
        private String value;
    }

    /**
     * 效果预览
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "算法效果预览")
    public static class Preview {

        @Schema(description = "预览场景")
        private String scene;

        @Schema(description = "替代文本")
        private String alt;

        @Schema(description = "预览框")
        private List<Box> boxes;
    }

    /**
     * 效果预览框
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "算法效果预览框")
    public static class Box {

        @Schema(description = "类别名称")
        private String label;

        @Schema(description = "置信度")
        private String confidence;

        @Schema(description = "X坐标百分比")
        private Integer x;

        @Schema(description = "Y坐标百分比")
        private Integer y;

        @Schema(description = "宽度百分比")
        private Integer width;

        @Schema(description = "高度百分比")
        private Integer height;
    }
}
