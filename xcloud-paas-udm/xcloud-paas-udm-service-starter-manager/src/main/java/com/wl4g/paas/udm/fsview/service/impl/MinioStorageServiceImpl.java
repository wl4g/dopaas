package com.wl4g.paas.udm.fsview.service.impl;

import com.wl4g.paas.udm.fsview.service.FsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author vjay
 * @date 2021-03-09 10:46:00
 */
@Configuration
@ConditionalOnProperty(name="doc.storage-type",havingValue = "minio")
public class MinioStorageServiceImpl implements FsService {

}
