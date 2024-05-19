package com.coCloud.server.modules.file.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * ClassName: DeleteFileContext
 * Description: 批量删除文件上下文实体对象
 *
 * @Author agility6
 * @Create 2024/5/19 21:20
 * @Version: 1.0
 */
@Data
public class DeleteFileContext implements Serializable {

    private static final long serialVersionUID = -3664403497882526157L;

    /**
     * 要删除的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前的登录用户ID
     */
    private Long userId;
}
