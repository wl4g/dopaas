/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.ci.common.hook;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-08-20 10:57:22
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class HookInfo {

    private String object_kind;
    private String event_name;
    private String before;
    private String after;
    private String ref;
    private String checkout_sha;
    private String message;
    private int user_id;
    private String user_name;
    private String user_username;
    private String user_email;
    private String user_avatar;
    private int project_id;
    private Project project;
    private List<Commits> commits;
    private int total_commits_count;
    private Repository repository;
    public void setObject_kind(String object_kind) {
         this.object_kind = object_kind;
     }
     public String getObject_kind() {
         return object_kind;
     }

    public void setEvent_name(String event_name) {
         this.event_name = event_name;
     }
     public String getEvent_name() {
         return event_name;
     }

    public void setBefore(String before) {
         this.before = before;
     }
     public String getBefore() {
         return before;
     }

    public void setAfter(String after) {
         this.after = after;
     }
     public String getAfter() {
         return after;
     }

    public void setRef(String ref) {
         this.ref = ref;
     }
     public String getRef() {
         return ref;
     }

    public void setCheckout_sha(String checkout_sha) {
         this.checkout_sha = checkout_sha;
     }
     public String getCheckout_sha() {
         return checkout_sha;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setUser_id(int user_id) {
         this.user_id = user_id;
     }
     public int getUser_id() {
         return user_id;
     }

    public void setUser_name(String user_name) {
         this.user_name = user_name;
     }
     public String getUser_name() {
         return user_name;
     }

    public void setUser_username(String user_username) {
         this.user_username = user_username;
     }
     public String getUser_username() {
         return user_username;
     }

    public void setUser_email(String user_email) {
         this.user_email = user_email;
     }
     public String getUser_email() {
         return user_email;
     }

    public void setUser_avatar(String user_avatar) {
         this.user_avatar = user_avatar;
     }
     public String getUser_avatar() {
         return user_avatar;
     }

    public void setProject_id(int project_id) {
         this.project_id = project_id;
     }
     public int getProject_id() {
         return project_id;
     }

    public void setProject(Project project) {
         this.project = project;
     }
     public Project getProject() {
         return project;
     }

    public void setCommits(List<Commits> commits) {
         this.commits = commits;
     }
     public List<Commits> getCommits() {
         return commits;
     }

    public void setTotal_commits_count(int total_commits_count) {
         this.total_commits_count = total_commits_count;
     }
     public int getTotal_commits_count() {
         return total_commits_count;
     }

    public void setRepository(Repository repository) {
         this.repository = repository;
     }
     public Repository getRepository() {
         return repository;
     }

    public static class Commits {
        private String id;
        private String message;
        private Date timestamp;
        private String url;
        private Author author;
        private List<String> added;
        private List<String> modified;
        private List<String> removed;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
        public Date getTimestamp() {
            return timestamp;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }
        public Author getAuthor() {
            return author;
        }

        public void setAdded(List<String> added) {
            this.added = added;
        }
        public List<String> getAdded() {
            return added;
        }

        public void setModified(List<String> modified) {
            this.modified = modified;
        }
        public List<String> getModified() {
            return modified;
        }

        public void setRemoved(List<String> removed) {
            this.removed = removed;
        }
        public List<String> getRemoved() {
            return removed;
        }

    }

    public static class Repository {

        private String name;
        private String url;
        private String description;
        private String homepage;
        private String git_http_url;
        private String git_ssh_url;
        private int visibility_level;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }
        public String getHomepage() {
            return homepage;
        }

        public void setGit_http_url(String git_http_url) {
            this.git_http_url = git_http_url;
        }
        public String getGit_http_url() {
            return git_http_url;
        }

        public void setGit_ssh_url(String git_ssh_url) {
            this.git_ssh_url = git_ssh_url;
        }
        public String getGit_ssh_url() {
            return git_ssh_url;
        }

        public void setVisibility_level(int visibility_level) {
            this.visibility_level = visibility_level;
        }
        public int getVisibility_level() {
            return visibility_level;
        }

    }

    public static class Project {

        private int id;
        private String name;
        private String description;
        private String web_url;
        private String avatar_url;
        private String git_ssh_url;
        private String git_http_url;
        private String namespace;
        private int visibility_level;
        private String path_with_namespace;
        private String default_branch;
        private String ci_config_path;
        private String homepage;
        private String url;
        private String ssh_url;
        private String http_url;
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setWeb_url(String web_url) {
            this.web_url = web_url;
        }
        public String getWeb_url() {
            return web_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }
        public String getAvatar_url() {
            return avatar_url;
        }

        public void setGit_ssh_url(String git_ssh_url) {
            this.git_ssh_url = git_ssh_url;
        }
        public String getGit_ssh_url() {
            return git_ssh_url;
        }

        public void setGit_http_url(String git_http_url) {
            this.git_http_url = git_http_url;
        }
        public String getGit_http_url() {
            return git_http_url;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
        public String getNamespace() {
            return namespace;
        }

        public void setVisibility_level(int visibility_level) {
            this.visibility_level = visibility_level;
        }
        public int getVisibility_level() {
            return visibility_level;
        }

        public void setPath_with_namespace(String path_with_namespace) {
            this.path_with_namespace = path_with_namespace;
        }
        public String getPath_with_namespace() {
            return path_with_namespace;
        }

        public void setDefault_branch(String default_branch) {
            this.default_branch = default_branch;
        }
        public String getDefault_branch() {
            return default_branch;
        }

        public void setCi_config_path(String ci_config_path) {
            this.ci_config_path = ci_config_path;
        }
        public String getCi_config_path() {
            return ci_config_path;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }
        public String getHomepage() {
            return homepage;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }

        public void setSsh_url(String ssh_url) {
            this.ssh_url = ssh_url;
        }
        public String getSsh_url() {
            return ssh_url;
        }

        public void setHttp_url(String http_url) {
            this.http_url = http_url;
        }
        public String getHttp_url() {
            return http_url;
        }

    }

    public static class Author {

        private String name;
        private String email;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }

    }
}