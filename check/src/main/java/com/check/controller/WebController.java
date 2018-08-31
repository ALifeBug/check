package com.check.controller;

import com.check.dao.*;
import com.check.entity.Check;
import com.check.entity.ClassRoom;
import com.check.entity.Course;
import com.check.entity.Student;
import com.check.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;



@Controller
@CrossOrigin(origins = "*")
@Transactional
public class WebController {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ClassRoomRepository classRoomRepository;

	@Autowired
	private CheckRepository checkRepository;

	@Autowired
	private RecordRepository recordRepository;

	protected Student getSessionStudent(HttpServletRequest request){
		return  (Student) request.getSession().getAttribute("student");
	}

	protected void setSessionStudent(HttpServletRequest request,Student student){
		request.getSession().setAttribute("student",student);
	}

	@ModelAttribute("student")
	public Student getStudent(){
		Student student = new Student();
		return student;
	}

	@RequestMapping("/RegisterForm")
	public ModelAndView RegisterForm(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		mav.setViewName("register");
		return mav;
	}

	@RequestMapping("/LoginForm")
	public String LoginForm(HttpServletRequest request) throws Exception{
		return "login";
	}

	@RequestMapping(value="/Login",method = RequestMethod.POST)
	public ModelAndView Login(HttpServletRequest request) throws Exception{
		int sno = Integer.parseInt(request.getParameter("sno"));
		String password = request.getParameter("password");
		ModelAndView mav =new ModelAndView();

		if(studentRepository.findBySno(sno)==null){
			mav.setViewName("login");
			mav.addObject("sno",sno);
			mav.addObject("password",password);
			mav.addObject("message","该用户不存在");
			return mav;
		}else if(studentRepository.findBySno(sno).getPassword().equals(password)==false){
			mav.setViewName("login");
			mav.addObject("sno",sno);
			mav.addObject("password",password);
			mav.addObject("message","密码不正确，请重新输入");
			return mav;
		}else{
			mav.setViewName("index");
			setSessionStudent(request,studentRepository.findBySno(sno));
			return mav;
		}


	}

	@RequestMapping(value="/Register",method = RequestMethod.POST)
	public ModelAndView Register( HttpServletRequest request){
		String name = request.getParameter("name");
		int sno = Integer.parseInt(request.getParameter("sno"));
		String gender = request.getParameter("gender");
		String macID = request.getParameter("macID");
		String password = request.getParameter("password");
		Student student = new Student(sno,macID,name,gender,password);
		ModelAndView mav = new ModelAndView();

		if(studentRepository.findBySno(sno)!=null ||
				studentRepository.findByMacID(macID)!=null){
			mav.setViewName("register");
			mav.addObject("macID",macID);
			mav.addObject("student",student);
			mav.addObject("message","该账户已被注册,请检查学号与MAC地址");
			return mav;
		}
		else{
			mav.addObject("student",student);
			mav.setViewName("success");
			studentRepository.save(student);
			setSessionStudent(request,student);
			return mav;
		}
	}

	@RequestMapping("/Logout")
	protected String Logout(HttpServletRequest request){
		if(getSessionStudent(request)==null){
			return "login";
		}else
		request.getSession().removeAttribute("student");
		return "index";
	}

	@RequestMapping("/Courses")
	public ModelAndView getCourses(@RequestParam int pageNum) throws Exception{
		ModelAndView mav = new ModelAndView();
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			Sort.Order order = new Sort.Order(Sort.Direction.ASC, "courseID");
			Sort sort = new Sort(order);
			Pageable page = new PageRequest(pageNum, 8, sort);
			Page<Course> courses = courseRepository.findAll(page);
			mav.addObject("page", courses);
			mav.setViewName("courses");
		}
			return mav;

	}

	@RequestMapping("/Search")
	public ModelAndView getCourse(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			String param = request.getParameter("param");
			List<Course> courses1 = courseRepository.findByCourseName(param);
			List<Course> courses2 = courseRepository.findByTname(param);

			if (courses1.isEmpty()) {
				mav.addObject("courses", courses2);
			} else {
				mav.addObject("courses", courses1);
			}
			mav.setViewName("results");
		}
		return mav;
	}

	@RequestMapping("/ShowCourse")
	public ModelAndView showCourse(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			int ID = Integer.parseInt(request.getParameter("ID"));
			Course course = courseRepository.findByCourseID(ID);
			Check check = checkRepository.findByCnoAndSno(ID, getSessionStudent(request).getSno());
			Boolean ifJoin = check != null;
			List<ClassRoom> classRooms = course.getClassrooms();
			String courseName = course.getCourseName();
			mav.addObject("ifJoin", ifJoin);
			mav.addObject("course", course);
			mav.addObject("classrooms", classRooms);
			mav.addObject("courseName",courseName);
			mav.addObject("page",request.getParameter("page"));
			mav.setViewName("showCourse");
		}
		return mav;
	}

	@RequestMapping("/Join")
	public ModelAndView JoinCourse(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			Student student = getSessionStudent(request);
			int sno = student.getSno();
			String sname = student.getName();
			int cno = Integer.parseInt(request.getParameter("cno"));
			int classroom = Integer.parseInt(request.getParameter("classroom"));
			String cname = courseRepository.findByCourseID(cno).getCourseName();
			int total = classRoomRepository.findByCnoAndNumber(cno, classroom).getTotal();
			Course course = courseRepository.findByCourseID(cno);
			List<ClassRoom> classRooms = course.getClassrooms();
			mav.addObject("course", course);
			mav.setViewName("showCourse");
			if (checkRepository.findByCnoAndSno(cno, sno) != null) {
				mav.addObject("message", "不能重复添加！");
				mav.addObject("ifJoin", true);
				mav.addObject("classrooms", classRooms);
				mav.addObject("page",request.getParameter("page"));
			} else {
				Check check = new Check(cno, cname, sno, sname, classroom, 0, total);
				checkRepository.save(check);
				ClassRoom classRoom = classRoomRepository.findByCnoAndNumber(cno, classroom);
				classRoom.setAmount(classRoom.getAmount() + 1);
				classRoomRepository.save(classRoom);
				mav.addObject("classrooms", classRooms);
				mav.addObject("ifJoin", true);
				mav.addObject("message", "添加成功！");
				mav.addObject("page",request.getParameter("page"));
			}
		}
		return mav;
	}

	@RequestMapping("/MyCourse")
	public ModelAndView MyCourse(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			Student student = getSessionStudent(request);
			List<Check> checks = checkRepository.findBySno(student.getSno());
			mav.addObject("checks", checks);
			mav.addObject("student", student.getName());
			mav.setViewName("myCourse");
		}
		return mav;
	}

	@RequestMapping("/RemoveCourse")
	public ModelAndView RemoveCourse(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			Student student = getSessionStudent(request);
			int cno = Integer.parseInt(request.getParameter("cno"));
			int sno = student.getSno();
			Check check = checkRepository.findByCnoAndSno(cno, sno);
			String message;
			if (check == null) {
				message = "该课程已经不在您的课表中";
			} else {
				check.getRecords().clear();
				recordRepository.deleteByCnoAndSno(cno, sno);
				checkRepository.deleteBySnoAndCno(sno, cno);
				message = "退课成功!";
				int number = check.getClassroom();
				ClassRoom classRoom = classRoomRepository.findByCnoAndNumber(cno, number);
				int amount = classRoom.getAmount() - 1;
				classRoom.setAmount(amount);
				classRoomRepository.save(classRoom);
			}
			List<Check> checks = checkRepository.findBySno(student.getSno());
			mav.addObject("checks", checks);
			mav.addObject("student", student.getName());
			mav.addObject("message", message);
			mav.setViewName("myCourse");
		}
		return mav;
	}

	@RequestMapping("/Record")
	public ModelAndView Record(HttpServletRequest request) throws Exception{
		ModelAndView mav =new ModelAndView();
		if(getSessionStudent(request)==null){
			mav.setViewName("login");
		}else {
			Student student = getSessionStudent(request);
			int cno = Integer.parseInt(request.getParameter("cno"));
			String cname = courseRepository.findByCourseID(cno).getCourseName();
			List<Record> records = recordRepository.findByCnoAndSnoOrderByNumber(cno, student.getSno());
			mav.addObject("records", records);
			mav.addObject("student", student.getName());
			mav.addObject("cname", cname);
			mav.setViewName("record");
		}
		return mav;
	}

}