package com.wl4g.devops.iam.common.web.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.support.cache.ScanCursor.CursorWrapper.parse;

/**
 * @author vjay
 * @date 2019-11-06 17:47:00
 */
public class SessionModelList {

    private List<SessionModel> sessions = new ArrayList<>(4);

    private CursorIndex index = new CursorIndex();


    public CursorIndex getIndex() {
        return index;
    }

    public void setIndex(CursorIndex index) {
        this.index = index;
    }

    public List<SessionModel> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionModel> sessions) {
        this.sessions = sessions;
    }

    public static class CursorIndex {

        private String cursorString;

        private Boolean hasNext = false;

        public String getCursorString() {
            return cursorString;
        }

        public void setCursorString(String cursorString) {
            if (StringUtils.isNotBlank(cursorString)) {
                parse(cursorString);
                this.cursorString = cursorString;
            }
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            if (Objects.nonNull(hasNext)) {
                this.hasNext = hasNext;
            }
        }
    }


}
