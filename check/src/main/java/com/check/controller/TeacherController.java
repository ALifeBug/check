package com.check.controller;


import com.alibaba.fastjson.JSONObject;
import com.check.dao.TeacherRepository;
import com.check.entity.Teacher;
import com.check.util.MjJSONUtil;
import com.check.util.MjStringUtil;
import com.check.util.RespMsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;





@RestController
@CrossOrigin(origins = "*")
@Transactional
@RequestMapping("/teacher")
public class TeacherController {
	
	@Autowired
	private TeacherRepository teacherRepository;
	
		//邮箱,密码,工号注册
		@RequestMapping(value="/add")
		protected JSONObject add(@RequestBody JSONObject jsonObject) throws IOException {
			JSONObject jo ;
			String password = jsonObject.getString("password");
			String email = jsonObject.getString("email");
			int tno = jsonObject.getIntValue("tno");
			String name = jsonObject.getString("name");
			String gender = jsonObject.getString("gender");
			if(jsonObject.isEmpty() ||  MjStringUtil.isEmpty(password) ||
					MjStringUtil.isEmpty(email) || MjStringUtil.isEmpty(tno) || MjStringUtil.isEmpty(name) || MjStringUtil.isEmpty(gender)){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			
			Teacher teacher = MjJSONUtil.jsonToBean(jsonObject,Teacher.class);
			if(teacherRepository.findByTno(teacher.getTno())!=null){
				return RespMsgUtil.getFailResponseJoWithErrMsg("该工号已经被注册");
			}
			
			try {
			teacher = teacherRepository.save(teacher);
			jo = RespMsgUtil.getSuccessResponseJoWithData(teacher);
			} catch (Exception e) {
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			return jo;
		}


		//工号查询
		@RequestMapping("/getByTno")
		protected JSONObject getByTno(@RequestBody JSONObject jsonObject) throws IOException {
			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(jsonObject.getIntValue("tno"))){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			if(teacherRepository.findByTno(jsonObject.getIntValue("tno"))==null){
				return (RespMsgUtil.getFailResponseJoErrGetNotExist());
			}
			Teacher teacher;
			try {
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			} catch (Exception e) {
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			if(teacher == null){
				return RespMsgUtil.getFailResponseJoErrGetNotExist();
			}else{
				return  RespMsgUtil.getSuccessResponseJoWithData(teacher);
			}
		}



		//登录
		@RequestMapping("/login")
		protected JSONObject login(@RequestBody JSONObject jsonObject) throws IOException{
		    int tno = jsonObject.getIntValue("tno");
            int password = jsonObject.getIntValue("password");
			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(tno) || MjStringUtil.isEmpty(password)){
				return (RespMsgUtil.getFailResponseJoErrJSONError());

			}
			if(teacherRepository.findByTno(jsonObject.getIntValue("tno"))==null){
				return (RespMsgUtil.getFailResponseJoErrGetNotExist());
			}
			Teacher teacher;
			String pwd;
			try {
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
				pwd = teacher.getPassword();
			} catch (Exception e) {
				e.printStackTrace();
				return (RespMsgUtil.getFailResponseJoErrHibernate());

			}
            if(Integer.parseInt(pwd)!= password){
                return RespMsgUtil.getFailResponseJoWithErrMsg("密码不正确");
            }
            else return RespMsgUtil.getSuccessResponseJo();
		}


		//更新
		@RequestMapping("/updateNameByTno")
		protected JSONObject updateNameByTno(@RequestBody JSONObject jsonObject) {
			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(jsonObject.getString("newName")) || MjStringUtil.isEmpty(jsonObject.getIntValue("tno"))){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			
			Teacher teacher;
			teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			if(teacher==null){
				return RespMsgUtil.getFailResponseJoErrUpdateNotExist();
			}
			try {
				teacherRepository.updateNameByTno(jsonObject.getString("newName"), jsonObject.getIntValue("tno"));
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			} catch (Exception e) {
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			return RespMsgUtil.getSuccessResponseJoWithData(teacher);
		}
		
		@RequestMapping("/updateSexByTno")
		protected JSONObject updateSexByTno(@RequestBody JSONObject jsonObject) {
			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(jsonObject.getString("newSex")) || MjStringUtil.isEmpty(jsonObject.getIntValue("tno"))){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			
			Teacher teacher;
			teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			if(teacher==null){
				return RespMsgUtil.getFailResponseJoErrUpdateNotExist();
			}
			try {
				teacherRepository.updateSexByTno(jsonObject.getString("newSex"), jsonObject.getIntValue("tno"));
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			return RespMsgUtil.getSuccessResponseJoWithData(teacher); 
		}

		@RequestMapping("/updatePasswordByTno")
		protected JSONObject updatePasswordByTno(@RequestBody JSONObject jsonObject) {
			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(jsonObject.getString("newPassword")) || MjStringUtil.isEmpty(jsonObject.getIntValue("tno"))){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			
			Teacher teacher;
			teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			if(teacher==null){
				return RespMsgUtil.getFailResponseJoErrUpdateNotExist();
			}
			try {
				teacherRepository.updatePasswordByTno(jsonObject.getString("newPassword"), jsonObject.getIntValue("tno"));
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			return RespMsgUtil.getSuccessResponseJoWithData(teacher); 
		}

		@RequestMapping("/updateByTno")
		protected JSONObject updateAllByTno(@RequestBody JSONObject jsonObject) {
				int tno = jsonObject.getIntValue("tno");
				String newName = jsonObject.getString("newName");
				String newSex = jsonObject.getString("newSex");
				String newPassword = jsonObject.getString("newPassword");
				String newEmail = jsonObject.getString("newEmail");

			if(jsonObject.isEmpty() || MjStringUtil.isEmpty(newPassword) || MjStringUtil.isEmpty(newName) ||
					MjStringUtil.isEmpty(tno) || MjStringUtil.isEmpty(newSex) || MjStringUtil.isEmpty(newEmail)){
				return RespMsgUtil.getFailResponseJoErrJSONError();
			}
			Teacher teacher;
			teacher = teacherRepository.findByTno(tno);
			if(teacher==null){
				return RespMsgUtil.getFailResponseJoErrUpdateNotExist();
			}
			try {
				teacher.setPassword(newPassword);
				teacher.setEmail(newEmail);
				teacher.setGender(newSex);
				teacher.setName(newName);
				teacherRepository.save(teacher);
				teacher = teacherRepository.findByTno(jsonObject.getIntValue("tno"));
			} catch (Exception e) {
				e.printStackTrace();
				return RespMsgUtil.getFailResponseJoErrHibernate();
			}
			return RespMsgUtil.getSuccessResponseJoWithData(teacher);
		}


}
