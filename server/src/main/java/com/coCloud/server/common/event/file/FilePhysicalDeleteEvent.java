package com.coCloud.server.common.event.file;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * ClassName: FilePhysicalDeleteEvent
 * Description: 文件被无力删除的事件实体
 *
 * @Author agility6
 * @Create 2024/7/7 20:57
 * @Version: 1.0
 */
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class FilePhysicalDeleteEvent extends ApplicationEvent {

    /**
     * 所有被物理删除的文件实体集合
     */
    private List<CoCloudUserFile> allRecords;

    public FilePhysicalDeleteEvent(Object source, List<CoCloudUserFile> allRecords) {
        super(source);
        this.allRecords = allRecords;
    }
}
