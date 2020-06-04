package com.wl4g.devops.ci.pcm.redmine.model;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 11:35:00
 */
public class RedmineTrackers {


    private List<RedmineTracker> trackers;

    public List<RedmineTracker> getTrackers() {
        return trackers;
    }

    public void setTrackers(List<RedmineTracker> trackers) {
        this.trackers = trackers;
    }

    public static class RedmineTracker{
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
