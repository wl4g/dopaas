package com.wl4g.devops.ci.utils;

import com.wl4g.components.common.cli.CommandUtils;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import static com.wl4g.devops.ci.utils.HookCommandHolder.CommandType.*;

/**
 * @author vjay
 * @date 2020-08-20 11:43:00
 */
public class HookCommandHolder {
    final private static SmartLogger log = SmartLoggerFactory.getLogger(HookCommandHolder.class);

    final private static String COMMAND_PRE = "$ci";

    final private static String BUILD_COMMAND = "build";
    final private static String DEPLOY_COMMAND = "deploy";

    /**
     * parse message: get hook command
     *
     * @param message
     * @return
     */
    public static HookCommand parse(String message) throws ParseException {
        String command = findCommand(message);
        if (StringUtils.isBlank(command)) {
            return null;
        }
        if (command.startsWith(BUILD_COMMAND)) {
            log.info("Parse build command");
            return parseBuild(command);
        } else if (command.startsWith(DEPLOY_COMMAND)) {
            log.info("Parse deploy command");
            return parseDeploy(command);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @param message like this: $ci deploy  —projects=datav,portal
     * @return like this: deploy  —projects=datav,portat
     */
    private static String findCommand(String message) {
        if (StringUtils.isBlank(message)) {
            return null;
        }
        String[] lines = message.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith(COMMAND_PRE)) {
                line = line.substring(COMMAND_PRE.length());
                line = line.trim();
                return line;
            }
        }
        return null;
    }


    private static BuildCommand parseBuild(String command) throws ParseException {
        String argStr = command.substring(BUILD_COMMAND.length());
        argStr = argStr.trim();
        CommandLine l = CommandUtils.newBuilder()
                .option(PROJECTS.getOpt(), PROJECTS.getLongOpt(), PROJECTS.getDefaultValue(), PROJECTS.getDescription())
                .option(BRANCH.getOpt(), BRANCH.getLongOpt(), BRANCH.getDefaultValue(), BRANCH.getDescription())
                .option(TEST.getOpt(), TEST.getLongOpt(), TEST.getDefaultValue(), TEST.getDescription())
                .build(argStr.split(" "));
        BuildCommand buildCommand = new BuildCommand();

        // is mvn test
        if (l.hasOption("test")) {
            buildCommand.setTest(true);
        }

        //projects
        String projectStr = l.getOptionValue(PROJECTS.getLongOpt());
        Assert2.hasTextOf(projectStr,"projectStr");
        String[] project = projectStr.split(",");
        buildCommand.setProjects(project);

        //branch
        buildCommand.setBranch(l.getOptionValue(BRANCH.getLongOpt()));

        return buildCommand;
    }

    private static DeployCommand parseDeploy(String command) throws ParseException {
        String argStr = command.substring(DEPLOY_COMMAND.length());
        argStr = argStr.trim();
        CommandLine l = CommandUtils.newBuilder()
                .option(PROJECTS.getOpt(), PROJECTS.getLongOpt(), PROJECTS.getDefaultValue(), PROJECTS.getDescription())
                .option(BRANCH.getOpt(), BRANCH.getLongOpt(), BRANCH.getDefaultValue(), BRANCH.getDescription())
                .option(TEST.getOpt(), TEST.getLongOpt(), TEST.getDefaultValue(), TEST.getDescription())
                .option(ENV.getOpt(), ENV.getLongOpt(), ENV.getDefaultValue(), ENV.getDescription())
                .build(argStr.split(" "));
        DeployCommand deployCommand = new DeployCommand();

        // is mvn test
        if (l.hasOption("test")) {
            deployCommand.setTest(true);
        }

        //projects
        String projectStr = l.getOptionValue(PROJECTS.getLongOpt());
        Assert2.hasTextOf(projectStr,"projectStr");
        String[] project = projectStr.split(",");
        deployCommand.setProjects(project);

        //branch
        deployCommand.setBranch(l.getOptionValue(BRANCH.getLongOpt()));

        deployCommand.setEnv(l.getOptionValue(ENV.getLongOpt()));

        return deployCommand;
    }


    public static interface HookCommand {

    }


    public static class BuildCommand implements HookCommand {
        private String[] projects;

        private boolean test;

        private String branch;

        public String[] getProjects() {
            return projects;
        }

        public void setProjects(String[] projects) {
            this.projects = projects;
        }

        public boolean isTest() {
            return test;
        }

        public void setTest(boolean test) {
            this.test = test;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }
    }

    public static class DeployCommand implements HookCommand {
        private String[] projects;

        private boolean test;

        private String env;

        private String branch;

        public String[] getProjects() {
            return projects;
        }

        public void setProjects(String[] projects) {
            this.projects = projects;
        }

        public boolean isTest() {
            return test;
        }

        public void setTest(boolean test) {
            this.test = test;
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }
    }


    public static enum CommandType {

        PROJECTS("p", "projects", null, "which projects need deploy"),
        BRANCH("b", "branch", "", "which branch need deploy"),
        ENV("e", "env", "", "deploy environment"),
        TEST("t", "test", "", "is projects need test");

        private String opt;
        private String longOpt;
        private String defaultValue;
        private String description;


        CommandType(String opt, String longOpt, String defaultValue, String description) {
            this.opt = opt;
            this.longOpt = longOpt;
            this.defaultValue = defaultValue;
            this.description = description;
        }

        public String getOpt() {
            return opt;
        }

        public String getLongOpt() {
            return longOpt;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public static CommandType getCommandTypeByOpt(String opt) {
            CommandType[] enums = CommandType.values();
            for (CommandType anEnum : enums) {
                if (StringUtils.equals(anEnum.getOpt(), opt)) {
                    return anEnum;
                }
            }
            return null;
        }


    }


}
