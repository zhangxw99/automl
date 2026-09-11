package org.jeecg.modules.automl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.automl.entity.AutomlModel;
import org.jeecg.modules.automl.mapper.AutomlModelMapper;
import org.jeecg.modules.automl.service.IAutomlModelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.Serializable;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 模型管理 Service
 */
@Slf4j
@Service
public class AutomlModelServiceImpl extends ServiceImpl<AutomlModelMapper, AutomlModel> implements IAutomlModelService {

    private static final Set<String> WEIGHT_EXTENSIONS = Set.of("pt", "onnx", "engine", "rknn", "torchscript", "bin", "zip", "tar");

    @Value("${jeecg.minio.minio_url}")
    private String minioUrl;

    @Value("${jeecg.minio.minio_name}")
    private String minioName;

    @Value("${jeecg.minio.minio_pass}")
    private String minioPass;

    @Value("${jeecg.minio.bucketName}")
    private String bucketName;

    private volatile MinioClient minioClient;

    @Override
    public AutomlModel uploadWeight(String id, MultipartFile file) throws Exception {
        AutomlModel model = getById(id);
        if (model == null) {
            throw new JeecgBootException("模型不存在");
        }
        if (file == null || file.isEmpty()) {
            throw new JeecgBootException("权重文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String filename = FilenameUtils.getName(originalFilename);
        String extension = FilenameUtils.getExtension(filename).toLowerCase(Locale.ROOT);
        if (!WEIGHT_EXTENSIONS.contains(extension)) {
            throw new JeecgBootException("不支持的权重文件类型：" + extension);
        }

        MinioClient client = client();
        ensureBucket(client, bucketName);

        String objectKey = "weights/" + model.getModelCode() + "/" + model.getVersion() + "/" + UUID.randomUUID() + "." + extension;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream inputStream = file.getInputStream();
             DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(digestInputStream, file.getSize(), -1L)
                    .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                    .build());
        } catch (Exception e) {
            removeObjectQuietly(bucketName, objectKey);
            throw e;
        }

        String oldBucket = model.getWeightBucket();
        String oldObjectKey = model.getWeightObjectKey();

        AutomlModel update = new AutomlModel();
        update.setId(model.getId());
        update.setWeightBucket(bucketName);
        update.setWeightObjectKey(objectKey);
        update.setWeightFileName(filename);
        update.setWeightFileSize(file.getSize());
        update.setWeightContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        update.setWeightChecksum(HexFormat.of().formatHex(digest.digest()));

        if (!updateById(update)) {
            removeObjectQuietly(bucketName, objectKey);
            throw new JeecgBootException("权重信息保存失败");
        }

        if (oldBucket != null && !oldBucket.isBlank() && oldObjectKey != null && !oldObjectKey.isBlank()) {
            removeObjectQuietly(oldBucket, oldObjectKey);
        }
        return getById(model.getId());
    }

    @Override
    public InputStream downloadWeight(String id) throws Exception {
        AutomlModel model = getById(id);
        if (model == null) {
            throw new JeecgBootException("模型不存在");
        }
        if (model.getWeightBucket() == null || model.getWeightObjectKey() == null) {
            throw new JeecgBootException("模型尚未上传权重");
        }
        return client().getObject(GetObjectArgs.builder()
                .bucket(model.getWeightBucket())
                .object(model.getWeightObjectKey())
                .build());
    }

    @Override
    public boolean removeById(Serializable id) {
        AutomlModel model = getById(id);
        if (model != null) {
            removeWeight(model);
        }
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(java.util.Collection<?> idList) {
        if (idList != null && !idList.isEmpty()) {
            List<Serializable> ids = idList.stream()
                    .map(id -> (Serializable) id)
                    .collect(Collectors.toList());
            listByIds(ids).forEach(this::removeWeight);
        }
        return super.removeByIds(idList);
    }

    private void removeWeight(AutomlModel model) {
        if (model.getWeightBucket() == null || model.getWeightObjectKey() == null) {
            return;
        }
        try {
            client().removeObject(RemoveObjectArgs.builder()
                    .bucket(model.getWeightBucket())
                    .object(model.getWeightObjectKey())
                    .build());
        } catch (Exception e) {
            log.error("删除模型权重失败，modelId={}", model.getId(), e);
            throw new JeecgBootException("删除模型权重失败");
        }
    }

    private MinioClient client() {
        if (minioClient == null) {
            synchronized (this) {
                if (minioClient == null) {
                    String endpoint = minioUrl;
                    if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                        endpoint = "http://" + endpoint;
                    }
                    minioClient = MinioClient.builder()
                            .endpoint(endpoint)
                            .credentials(minioName, minioPass)
                            .build();
                }
            }
        }
        return minioClient;
    }

    private void ensureBucket(MinioClient client, String bucket) throws Exception {
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private void removeObjectQuietly(String bucket, String objectKey) {
        try {
            client().removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("清理MinIO临时权重失败，bucket={}，objectKey={}", bucket, objectKey, e);
        }
    }
}
