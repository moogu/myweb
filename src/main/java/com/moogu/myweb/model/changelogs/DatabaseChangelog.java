package com.moogu.myweb.model.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;

@ChangeLog
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "someChangeId", author = "testAuthor")
    public void importantWorkToDo(DB db) {
        // task implementation
    }
}
