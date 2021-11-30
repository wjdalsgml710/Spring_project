package com.example.sproject.service.login;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.example.sproject.model.login.Member;

public interface LoginService extends UserDetailsService {
	int join(Member member);
	int passwordCheck(Member member);
	List<Member> getSessionMembers();
	int welcomeLogin(String m_id);
	int updateMemberPhoto(String m_id, MultipartFile multipartFile);
}
