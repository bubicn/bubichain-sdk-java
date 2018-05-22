package cn.bubi.baas.utils.http.agent;

import cn.bubi.baas.utils.http.HttpMethod;
import cn.bubi.baas.utils.http.RequestParamFilter;

import java.util.Properties;

public class NullRequestParamFilter implements RequestParamFilter{

    public static RequestParamFilter INSTANCE = new NullRequestParamFilter();

    private NullRequestParamFilter(){
    }

    @Override
    public void filter(HttpMethod requestMethod, Properties requestParams){
    }

}
