/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.uci.pcm.redmine.model;

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