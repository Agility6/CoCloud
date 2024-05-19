package com.coCloud.server.modules.file.context;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: UpdateFilenameContext
 * Description: 文件重命名参数上文下对象
 *
 * @Author agility6
 * @Create 2024/5/19 19:30
 * @Version: 1.0
 */
@Data
public class UpdateFilenameContext implements Serializable {

    private static final long serialVersionUID = 7548248745864321345L;

    /**
     * 要更新的文件ID
     */
    private Long fileId;

    /**
     * 新的文件名称
     */
    private String newFilename;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    /**
     * 要更新的文件记录实体
     */
    private CoCloudUserFile entity;
}
