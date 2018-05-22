package cn.bubi.access.adaptation.blockchain.bc.response.converter;

import cn.bubi.baas.utils.http.HttpServiceContext;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.agent.ServiceRequest;
import cn.bubi.baas.utils.http.converters.JsonResponseConverter;

import java.io.InputStream;

public abstract class AbstractResponseConverter implements ResponseConverter{
    private JsonResponseConverter jsonResponseConverter = new JsonResponseConverter(ServiceResponse.class);

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception{
        ServiceResponse serviceResponse = (ServiceResponse) jsonResponseConverter.getResponse(request, responseStream, null);
        //		if (serviceResponse == null ||! "0".equals(serviceResponse.getErrorCode())) {
        //			throw new RuntimeException("errorCode:"+serviceResponse.getErrorCode());
        //		}
        return dealResult(serviceResponse);
    }

    public abstract Object dealResult(ServiceResponse serviceResponse);

}
