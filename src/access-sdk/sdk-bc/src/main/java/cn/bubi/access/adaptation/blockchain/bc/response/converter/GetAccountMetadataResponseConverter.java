package cn.bubi.access.adaptation.blockchain.bc.response.converter;

import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.baas.utils.http.util.SerializeUtils;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 下午3:05.
 */
public class GetAccountMetadataResponseConverter extends AbstractResponseConverter{

    @Override
    public Object dealResult(ServiceResponse serviceResponse){
        Account account = SerializeUtils.deserializeAs(serviceResponse.getResult(), Account.class);
        if (account.getMetadatas() == null || account.getMetadatas().length < 1) {
            return null;
        }
        return account.getMetadatas()[0];
    }

}
