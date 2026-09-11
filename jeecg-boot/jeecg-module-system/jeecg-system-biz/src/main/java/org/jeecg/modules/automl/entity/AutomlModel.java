package org.jeecg.modules.automl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 模型管理
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "模型管理")
@TableName("automl_model")
public class AutomlModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    /** 模型名称 */
    @Schema(description = "模型名称")
    private String modelName;

    /** 模型编码 */
    @Schema(description = "模型编码")
    private String modelCode;

    /** 版本 */
    @Schema(description = "版本")
    private String version;

    /** 任务类型 */
    @Schema(description = "任务类型")
    private String taskType;

    /** 框架 */
    @Schema(description = "框架")
    private String framework;

    /** 状态 */
    @Schema(description = "状态")
    private String status;

    /** 说明 */
    @Schema(description = "说明")
    private String description;

    /** MinIO bucket */
    @Schema(description = "MinIO bucket")
    private String weightBucket;

    /** MinIO对象Key */
    @Schema(description = "MinIO对象Key")
    private String weightObjectKey;

    /** 原始权重文件名 */
    @Schema(description = "原始权重文件名")
    private String weightFileName;

    /** 权重文件大小 */
    @Schema(description = "权重文件大小")
    private Long weightFileSize;

    /** 权重文件类型 */
    @Schema(description = "权重文件类型")
    private String weightContentType;

    /** 权重SHA-256 */
    @Schema(description = "权重SHA-256")
    private String weightChecksum;
}
