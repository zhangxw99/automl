package org.jeecg.modules.automl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.automl.entity.AutomlModel;
import org.jeecg.modules.automl.service.IAutomlModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 模型管理
 */
@Slf4j
@Tag(name = "模型管理")
@RestController
@RequestMapping("/automl/model")
public class AutomlModelController {

    @Autowired
    private IAutomlModelService automlModelService;

    /**
     * 分页列表
     */
    @AutoLog(value = "模型管理-分页列表查询")
    @Operation(summary = "模型管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AutomlModel>> queryPageList(AutomlModel automlModel,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<AutomlModel> queryWrapper = QueryGenerator.initQueryWrapper(automlModel, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<AutomlModel> page = new Page<>(pageNo, pageSize);
        return Result.ok(automlModelService.page(page, queryWrapper));
    }

    /**
     * 添加
     */
    @AutoLog(value = "模型管理-添加")
    @Operation(summary = "模型管理-添加")
    @PostMapping(value = "/add")
    public Result<AutomlModel> add(@RequestBody AutomlModel automlModel) {
        if (automlModel.getModelName() == null || automlModel.getModelName().isBlank()) {
            return Result.error("模型名称不能为空");
        }
        if (automlModel.getModelCode() == null || automlModel.getModelCode().isBlank()) {
            return Result.error("模型编码不能为空");
        }
        if (automlModel.getVersion() == null || automlModel.getVersion().isBlank()) {
            return Result.error("模型版本不能为空");
        }
        automlModel.setWeightBucket(null);
        automlModel.setWeightObjectKey(null);
        automlModel.setWeightFileName(null);
        automlModel.setWeightFileSize(null);
        automlModel.setWeightContentType(null);
        automlModel.setWeightChecksum(null);
        automlModelService.save(automlModel);
        return Result.ok(automlModel);
    }

    /**
     * 编辑
     */
    @AutoLog(value = "模型管理-编辑")
    @Operation(summary = "模型管理-编辑")
    @PostMapping(value = "/edit")
    public Result<AutomlModel> edit(@RequestBody AutomlModel automlModel) {
        AutomlModel entity = automlModelService.getById(automlModel.getId());
        if (entity == null) {
            return Result.error("未找到对应实体");
        }
        automlModel.setWeightBucket(entity.getWeightBucket());
        automlModel.setWeightObjectKey(entity.getWeightObjectKey());
        automlModel.setWeightFileName(entity.getWeightFileName());
        automlModel.setWeightFileSize(entity.getWeightFileSize());
        automlModel.setWeightContentType(entity.getWeightContentType());
        automlModel.setWeightChecksum(entity.getWeightChecksum());
        automlModelService.updateById(automlModel);
        return Result.ok(automlModelService.getById(automlModel.getId()));
    }

    /**
     * 删除
     */
    @AutoLog(value = "模型管理-删除")
    @Operation(summary = "模型管理-删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        automlModelService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "模型管理-批量删除")
    @Operation(summary = "模型管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        automlModelService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "模型管理-通过id查询")
    @Operation(summary = "模型管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AutomlModel> queryById(@RequestParam(name = "id") String id) {
        AutomlModel model = automlModelService.getById(id);
        if (model == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(model);
    }

    /**
     * 上传权重到MinIO
     */
    @AutoLog(value = "模型管理-上传权重")
    @Operation(summary = "模型管理-上传权重到MinIO")
    @PostMapping(value = "/{id}/weight")
    public Result<AutomlModel> uploadWeight(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            return Result.ok(automlModelService.uploadWeight(id, file));
        } catch (Exception e) {
            log.error("上传模型权重失败，modelId={}", id, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从MinIO下载权重
     */
    @AutoLog(value = "模型管理-下载权重")
    @Operation(summary = "模型管理-从MinIO下载权重")
    @GetMapping(value = "/{id}/weight")
    public void downloadWeight(@PathVariable String id, HttpServletResponse response) {
        try {
            AutomlModel model = automlModelService.getById(id);
            if (model == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String filename = model.getWeightFileName() == null ? "model-weight" : model.getWeightFileName();
            response.setContentType(model.getWeightContentType() == null ? "application/octet-stream" : model.getWeightContentType());
            if (model.getWeightFileSize() != null && model.getWeightFileSize() >= 0) {
                response.setContentLengthLong(model.getWeightFileSize());
            }
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));

            try (InputStream inputStream = automlModelService.downloadWeight(id);
                 OutputStream outputStream = response.getOutputStream()) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("下载模型权重失败，modelId={}", id, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
