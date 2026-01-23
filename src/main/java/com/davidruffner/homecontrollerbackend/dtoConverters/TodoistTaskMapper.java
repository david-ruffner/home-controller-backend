package com.davidruffner.homecontrollerbackend.dtoConverters;

import com.davidruffner.homecontrollerbackend.cache.UserSettingsContext;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverTask;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverTaskRaw;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.ZDTDuration;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.utils.Utils.ZDTTime;
import org.springframework.stereotype.Component;

import static com.davidruffner.homecontrollerbackend.utils.Utils.getZDTFromTimestamp;

@Component
public class TodoistTaskMapper {

    private final UserSettingsContext userSettingsCtx;

    public TodoistTaskMapper(UserSettingsContext userSettingsCtx) {
        this.userSettingsCtx = userSettingsCtx;
    }

    public TodoistRetrieverTask toEnriched(TodoistRetrieverTaskRaw raw) {
        UserSettings userSettings = this.userSettingsCtx.getRequired();

        ZDTTime deadline = raw.deadline() == null ? null :
            getZDTFromTimestamp(raw.deadline().date(), userSettings);

        ZDTDuration duration = null;
        if (raw.duration() != null && raw.due() != null) {
            duration = ZDTDuration.from(raw.duration(), raw.due(), userSettings);
        }

        return new TodoistRetrieverTask(
            raw.taskId(),
            raw.projectId(),
            raw.sectionId(),
            raw.parentId(),
            raw.labels(),
            deadline,
            duration,
            raw.due(),
            raw.priority(),
            raw.childOrder(),
            raw.content(),
            raw.description(),
            raw.noteCount()
        );
    }
}
