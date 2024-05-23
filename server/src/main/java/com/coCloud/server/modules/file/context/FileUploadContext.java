package com.coCloud.server.modules.file.context;

import com.coCloud.server.modules.file.entity.CoCloudFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * ClassName: FileUploadPO
 * Description: 单文件上传的上下文实体
 *
 * @Author agility6
 * @Create 2024/5/22 23:29
 * @Version: 1.0
 */
@Data
public class FileUploadContext implements Serializable {

    private static final long serialVersionUID = 2818398307203082669L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 文件的父文件夹ID
     */
    private Long parentId;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 实体文件记录
     */
    private CoCloudFile record;
}
