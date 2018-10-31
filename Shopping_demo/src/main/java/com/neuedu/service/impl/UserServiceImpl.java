package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {

        //1、参数的非空校验
        if (username==null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if (password==null || password.equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }

        //2、校验用户名是否存在
        int result=userInfoMapper.checkUsername(username);
        if (result==0){
            return ServerResponse.serverResponseByError("用户名不存在");
        }
        //3、根据用户名和密码查找用户信息
       UserInfo userInfo=userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(password));

        //4、返回结果
        if(userInfo==null){
            return ServerResponse.serverResponseByError("密码错误");
        }
        userInfo.setPassword("");
        return ServerResponse.serverResponseBySuccess(userInfo);

    }

    @Override
    public ServerResponse register(UserInfo userInfo) {

        //1、参数的非空校验
        if (userInfo==null){
            return ServerResponse.serverResponseByError("参数必须");
        }
        //2、检验用户名是否存在
        int result=userInfoMapper.checkUsername(userInfo.getUsername());
        if(result>0){
            return ServerResponse.serverResponseByError("用户名已存在");
        }

        //3、校验邮箱是否存在
        int result_email=userInfoMapper.checkEmail(userInfo.getEmail());
        if (result_email>0){
            return ServerResponse.serverResponseByError("邮箱已存在");
        }

        //4、注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count=userInfoMapper.insert(userInfo);
        if(count>0){
            return ServerResponse.serverResponseBySuccess("注册成功");
        }

        //5、返回结果
        return ServerResponse.serverResponseByError("注册失败");
    }

    @Override
    public ServerResponse forget_get_question(String username) {

        //1、参数校验
        if (username==null || username==""){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        //2、校验用户名
        int result=userInfoMapper.checkUsername(username);
        if (result==0){
            return ServerResponse.serverResponseByError("用户名不存在,请重新输入");
        }

        //3、查找密保问题
        String question=userInfoMapper.selectQuestionByUsername(username);
        if (question==null || question.equals("")){
            return ServerResponse.serverResponseByError("密保问题空");
        }

        return ServerResponse.serverResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {

        //1、参数校验
        if (username==null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if (question==null || question.equals("")){
            return ServerResponse.serverResponseByError("问题不能为空");
        }
        if (answer==null || answer.equals("")){
            return ServerResponse.serverResponseByError("答案不能为空");
        }
        //2、根据username，question，answer查询
        int result=userInfoMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
        if (result==0){
            return ServerResponse.serverResponseByError("答案错误");
        }
        //3、服务端生成一个token保存并将token返回到客户端
        String forgetToken=UUID.randomUUID().toString();
        //谷歌的guava
        TokenCache.set(username,forgetToken);


        return ServerResponse.serverResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew, String forgetToken) {

        //1、参数校验
        if (username==null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if (passwordNew==null || passwordNew.equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }
        if (forgetToken==null || forgetToken.equals("")){
            return ServerResponse.serverResponseByError("token不能为空");
        }
        //2、token校验
        String token=TokenCache.get(username);
        if (token==null){
            return ServerResponse.serverResponseByError("token过期了");
        }
        if (!token.equals(forgetToken)){
            return ServerResponse.serverResponseByError("无效的token");
        }

        //3、修改密码
        int result=userInfoMapper.updateUserPassword(username,MD5Utils.getMD5Code(passwordNew));
        if (result>0){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("密码修改失败");

    }
}
