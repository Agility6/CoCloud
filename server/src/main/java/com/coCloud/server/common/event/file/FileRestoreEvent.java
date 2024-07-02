package com.coCloud.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * ClassName: FileRestoreEvent
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/2 23:07
 * @Version: 1.0
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class FileRestoreEvent extends ApplicationEvent {

    /**
     * 被成功还原的文件记录ID集合
     */
    private List<Long> fileIdList;

    public FileRestoreEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }
}
