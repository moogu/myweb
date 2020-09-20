package com.moogu.myweb.model.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;
import lombok.extern.slf4j.Slf4j;

@ChangeLog
@Slf4j
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "someChangeId", author = "Patrick Santana")
    public void importantWorkToDo(DB db) {
        // task implementation
        log.debug(String.format("Database name %s", db.getName()));
    }
}
