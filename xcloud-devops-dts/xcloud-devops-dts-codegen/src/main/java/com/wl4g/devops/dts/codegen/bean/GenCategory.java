package com.wl4g.devops.dts.codegen.bean;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;

/**
 * @author vjay
 * @date 2020-09-14 15:27:00
 */
public enum GenCategory {

    JUST_DAO(new String[]{MAPPER}),

    JUST_VUEJS(new String[]{VUEJS}),

    JUST_AGJS(new String[]{AGJS}),

    DAO_SERVICE_CONTRELLER(new String[]{MAPPER, MVN_SPINGCLOUD}),

    DAO_SERVICE_CONTRELLER_VUE(new String[]{MAPPER, MVN_SPINGCLOUD, VUEJS});

    private final String[] providers;

    GenCategory(String[] providers) {
        this.providers = providers;
    }

    public String[] getProviders() {
        return providers;
    }

    public static String[] getProvidersByTplCategory(String tplCategory) {
        for (GenCategory anEnum : values()) {
            if (anEnum.name().equals(tplCategory)) {
                return anEnum.getProviders();
            }
        }
        return null;
    }

    public static List<String> getAllTplCategory() {
        List<String> list = new ArrayList<String>();
        GenCategory[] enums = GenCategory.values();
        for (int i = 0; i < enums.length; i++) {
            list.add(enums[i].name());
        }
        return list;
    }

}
