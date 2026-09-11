package org.jeecg.modules.automl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.automl.entity.AutomlModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 模型管理 Service
 */
public interface IAutomlModelService extends IService<AutomlModel> {

    /**
     * 上传模型权重到 MinIO
     */
    AutomlModel uploadWeight(String id, MultipartFile file) throws Exception;

    /**
     * 从 MinIO 读取模型权重
     */
    InputStream downloadWeight(String id) throws Exception;
}
