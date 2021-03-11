<div>

    <!-- 接口基础信息 -->
    <div class="baseInfo">
        <span class="apiNameClass">
            ${apiName}
        </span><br>

        <span class="infoTitle">接口ID:</span>
        <span class="infoValue">
            ${id}
        </span><br>

        <span class="infoTitle">地址:</span>
        <span class="infoValue">
            ${path}
        </span><br>

        <span class="infoTitle">类型:</span>
        <span class="infoValue">
            ${method}
        </span>
    </div>

    <!-- 请求参数 -->
    <div>
        <span>请求参数</span>
        <table>
            <tr>
                <th>名称</th>
                <th>必选</th>
                <th>类型</th>
                <th>初始值</th>
                <th>简介</th>
            </tr>
            <#if requestProperties?? && requestProperties?size gt 0>
                <@propertiesTree properties=requestProperties deep=0></@propertiesTree>
            </#if>
        </table>
    </div>

    <!-- 响应参数 -->
    <div>
        <span>响应参数</span>
        <table>
            <tr>
                <th>名称</th>
                <th>必选</th>
                <th>类型</th>
                <th>初始值</th>
                <th>简介</th>
            </tr>
            <#if responseProperties?? && responseProperties?size gt 0>
                <@propertiesTree properties=responseProperties deep=0></@propertiesTree>
            </#if>
        </table>
    </div>

</div>