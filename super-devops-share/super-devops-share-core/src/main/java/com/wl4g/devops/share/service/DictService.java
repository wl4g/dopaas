package com.wl4g.devops.share.service;

import com.wl4g.devops.common.bean.share.Dict;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-08-13 09:51:00
 */
public interface DictService {

    void save(Dict dict);

    void del(Integer id);

    List<Dict> getBytype(String type);

    Dict getByKey(String key);

    List<String> allType();

    Map<String,Object> cache();

}
