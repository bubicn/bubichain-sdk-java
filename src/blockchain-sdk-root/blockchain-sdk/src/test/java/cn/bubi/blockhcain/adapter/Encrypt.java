/*
Copyright Bubi Technologies Co., Ltd. 2017 All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package cn.bubi.blockhcain.adapter;

import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
  
public class Encrypt  
{  
  
  /** 
   * 传入文本内容，返回 SHA-256 串 
   *  
   * @param strText 
   * @return 
   */  
static public String SHA256(final byte[] strText)  
  {  
    return SHA(strText, "SHA-256");  
  }  
  
  /** 
   * 传入文本内容，返回 SHA-512 串 
   *  
   * @param strText 
   * @return 
   */  
  static public String SHA512(final byte[] strText)  
  {  
    return SHA(strText, "SHA-512");  
  }  
  
  /** 
   * 字符串 SHA 加密 
   *  
   * @param strSourceText 
   * @return 
   */  
  static private String SHA(final byte[] text, final String strType)  
  {  
    // 返回值  
    String strResult = null;  
  
    // 是否是有效字符串  
    if (text != null && text.length > 0)  
    {  
      try  
      {  
        // SHA 加密开始  
        // 创建加密对象 并傳入加密類型  
        MessageDigest messageDigest = MessageDigest.getInstance(strType);  
        // 传入要加密的字符串  
        messageDigest.update(text);  
        // 得到 byte 類型结果  
        byte byteBuffer[] = messageDigest.digest();  
  
        // 將 byte 轉換爲 string  
        StringBuffer strHexString = new StringBuffer();  
        // 遍歷 byte buffer  
        for (int i = 0; i < byteBuffer.length; i++)  
        {  
          String hex = Integer.toHexString(0xff & byteBuffer[i]);  
          if (hex.length() == 1)  
          {  
            strHexString.append('0');  
          }  
          strHexString.append(hex);  
        }  
        // 得到返回結果  
        strResult = strHexString.toString();  
      }  
      catch (NoSuchAlgorithmException e)  
      {  
        e.printStackTrace();  
      }  
    }  
  
    return strResult;  
  }  
}  
