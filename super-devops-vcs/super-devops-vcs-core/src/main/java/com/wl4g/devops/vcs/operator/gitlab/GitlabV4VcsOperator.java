/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.vcs.operator.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.AbstractVcsOperator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * VCS operator for GITLAB V4.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public class GitlabV4VcsOperator extends AbstractVcsOperator {

    @Override
    public VcsProviderKind kind() {
        return VcsProviderKind.GITLAB;
    }

    @Override
    protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
        return new HttpEntity<>(null, headers);
    }

    @Override
    public List<GitlabV4BranchModel> getRemoteBranchs(Vcs credentials, int projectId) {
        super.getRemoteBranchs(credentials, projectId);

        String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/branches";
        // Extract branch names.
        List<GitlabV4BranchModel> branchs = doRemoteExchange(credentials, url, null, new TypeReference<List<GitlabV4BranchModel>>() {
        });


        if (log.isInfoEnabled()) {
            log.info("Extract remote branch names: {}", branchs);
        }
        return branchs;
    }

    @Override
    public List<GitlabV4TagModel> getRemoteTags(Vcs credentials, int projectId) {
        super.getRemoteTags(credentials, projectId);

        String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/tags";
        // Extract tag names.
        List<GitlabV4TagModel> tags = doRemoteExchange(credentials, url, null, new TypeReference<List<GitlabV4TagModel>>() {
        });
        if (log.isInfoEnabled()) {
            log.info("Extract remote tag names: {}", tags);
        }
        return tags;
    }

    @Override
    public GitlabV4BranchModel createRemoteBranch(Vcs credentials, int projectId, String branch, String ref) {
        super.createRemoteBranch(credentials, projectId, branch, ref);
        String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/branches?branch=%s&ref=%s";

        return doRemotePost(credentials, String.format(url, branch, ref), null, new TypeReference<GitlabV4BranchModel>() {
        });
    }

    @Override
    public GitlabV4TagModel createRemoteTag(Vcs credentials, int projectId, String tag, String ref, String message, String releaseDescription) {
        super.createRemoteTag(credentials,projectId,tag,ref,message,releaseDescription);
        String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/tags?tag_name=%s&ref=%s&message=%s&release_description=%s";

        return doRemotePost(credentials, String.format(url, tag, ref,message,releaseDescription), null, new TypeReference<GitlabV4TagModel>() {});
    }

    @Override
    public Integer getRemoteProjectId(Vcs credentials, String projectName) {
        super.getRemoteProjectId(credentials, projectName);

        // Search projects for GITLAB.
        List<GitlabV4SimpleProjectModel> projects = searchRemoteProjects(credentials, null, projectName, null);
        Integer id = null;
        for (GitlabV4SimpleProjectModel p : projects) {
            if (trimToEmpty(projectName).equals(p.getName())) {
                id = p.getId();
                break;
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Extract remote project IDs: {}", id);
        }
        return id;
    }

    @Override
    public List<GitlabV4SimpleGroupModel> searchRemoteGroups(Vcs credentials, String groupName, int limit) {
        String url = String.format((credentials.getBaseUri() + "/api/v4/groups?search=%s&per_page=%s"), groupName, limit);
        List<GitlabV4SimpleGroupModel> groups = doRemoteExchange(credentials, url, null,
                new TypeReference<List<GitlabV4SimpleGroupModel>>() {
                });
        groups = safeList(groups);
        List<GitlabV4SimpleGroupModel> gitlabV4SimpleGroupModels = group2Tree(groups);
        return gitlabV4SimpleGroupModels;
    }

    private List<GitlabV4SimpleGroupModel> group2Tree(List<GitlabV4SimpleGroupModel> groups) {
        List<GitlabV4SimpleGroupModel> top = new ArrayList<>();
        for (GitlabV4SimpleGroupModel group : groups) {
            if (Objects.isNull(group.getParent_id())) {
                top.add(group);
            }
        }
        for (GitlabV4SimpleGroupModel t : top) {
            addChild(groups, t);
        }
        return top;
    }

    private void addChild(List<GitlabV4SimpleGroupModel> groups, GitlabV4SimpleGroupModel parent) {
        for (GitlabV4SimpleGroupModel group : groups) {
            if (parent.getId().equals(group.getParent_id())) {
                List<GitlabV4SimpleGroupModel> children = parent.getChildren();
                if (Objects.isNull(children)) {
                    children = new ArrayList<>();
                }
                children.add(group);
                parent.setChildren(children);
                addChild(groups, group);
            }
        }
    }

    @Override
    public List<GitlabV4SimpleProjectModel> searchRemoteProjects(Vcs credentials, Integer groupId, String projectName, int limit, PageModel pm) {
        super.searchRemoteProjects(credentials, groupId, projectName, limit, pm);

        // Parameters correcting.
        if (isBlank(projectName)) {
            projectName = EMPTY;
        }
        if (nonNull(pm) && nonNull(pm.getPageSize())) {
            limit = pm.getPageSize();
        } else {
            limit = 10;
        }
        String url;
        if (nonNull(groupId)) {
            // Search of remote URL.
            url = String.format((credentials.getBaseUri() + "/api/v4/groups/%d/projects?simple=true&search=%s&per_page=%s&page=%s"), groupId, projectName, limit,pm.getPageNum());
        } else {
            // Search of remote URL.
            url = String.format((credentials.getBaseUri() + "/api/v4/projects?simple=true&search=%s&per_page=%s&page=%s"), projectName, limit,pm.getPageNum());
        }

        HttpHeaders headers = new HttpHeaders();
        // Search projects.
        List<GitlabV4SimpleProjectModel> projects = doRemoteExchange(credentials, url, headers,
                new TypeReference<List<GitlabV4SimpleProjectModel>>() {
                });
        if (nonNull(pm)) {
            pm.setTotal(Long.valueOf(headers.getFirst("X-Total")));
        }
        return safeList(projects);

    }

    @Override
    public GitlabV4ProjectModel searchRemoteProjectsById(Vcs credentials, Integer projectId) {
        Assert2.notNullOf(projectId, "projectId");
        String url = String.format((credentials.getBaseUri() + "/api/v4/projects/%d"), projectId);
        GitlabV4ProjectModel project = doRemoteExchange(credentials, url, null,
                new TypeReference<GitlabV4ProjectModel>() {
                });
        return project;
    }
}